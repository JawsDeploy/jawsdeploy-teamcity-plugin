package net.jawsdeploy.teamcity

import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine
import net.jawsdeploy.teamcity.http.JawsDeployApiClient
import net.jawsdeploy.teamcity.model.*
import net.jawsdeploy.teamcity.shared.JawsDeployRunnerConstants

class JawsDeployBuildService : BuildServiceAdapter() {
  @Volatile private var interrupted = false
  @Volatile private var result: BuildFinishedStatus = BuildFinishedStatus.FINISHED_SUCCESS

  override fun beforeProcessStarted() {
    val logger = build.buildLogger
    logger.progressStarted("JawsDeploy")
    try {
      runInternal()
    } catch (t: Throwable) {
      logger.error("JawsDeploy step failed: ${t.message}")
      result = BuildFinishedStatus.FINISHED_FAILED
    } finally {
      logger.progressFinished()
    }
  }

  private fun runInternal() {
    val p = runnerParameters
    val logger = build.buildLogger

    val api = JawsDeployApiClient(
      p[JawsDeployRunnerConstants.PARAM_API_BASE_URL].orEmpty(),
      p[JawsDeployRunnerConstants.PARAM_LOGIN].orEmpty(),
      p[JawsDeployRunnerConstants.PARAM_API_KEY].orEmpty(),
      p[JawsDeployRunnerConstants.PARAM_REQUEST_TIMEOUT_MS]?.toLongOrNull() ?: 120_000L
    )

    val projectId = p[JawsDeployRunnerConstants.PARAM_PROJECT_ID].orEmpty()
    require(projectId.isNotBlank()) { "Project ID is required" }

    val version = p[JawsDeployRunnerConstants.PARAM_VERSION]?.takeIf { it.isNotBlank() } ?: build.buildNumber
    val phaseName = p[JawsDeployRunnerConstants.PARAM_PHASE_NAME]?.takeIf { it.isNotBlank() }
    val envs = p[JawsDeployRunnerConstants.PARAM_ENVIRONMENTS]
      ?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }
    val redownload = p[JawsDeployRunnerConstants.PARAM_REDOWNLOAD_PACKAGES]?.equals("true", true)
    val excludes = p[JawsDeployRunnerConstants.PARAM_EXCLUDE_STEP_NAMES]
      ?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }
    val pollMs = p[JawsDeployRunnerConstants.PARAM_POLL_INTERVAL_MS]?.toLongOrNull() ?: 2000L

    val op = (p[JawsDeployRunnerConstants.PARAM_OPERATION] ?: "createAndDeploy").lowercase()
    require(op == "createanddeploy" || op == "promote") { "Operation must be 'createAndDeploy' or 'promote'" }

    val deploymentIds: List<String> = when (op) {
      "createanddeploy" -> {
        logger.message("Creating release $version for project $projectId …")
        val createReq = CreateReleaseRequest(
          version = version,
          projectId = projectId,
          channelName = p[JawsDeployRunnerConstants.PARAM_CHANNEL_NAME]?.takeIf { it.isNotBlank() },
          ignoreDefaultChannel = p[JawsDeployRunnerConstants.PARAM_IGNORE_DEFAULT_CHANNEL]?.equals("true", true),
          notes = p[JawsDeployRunnerConstants.PARAM_NOTES]?.takeIf { it.isNotBlank() }
        )
        val createResp = api.parse<CreateReleaseResponse>(api.postJson("/release", createReq))
        logger.message("Created release: ${'$'}{createResp.releaseId}")

        val deployReq = DeployReleaseRequest(
          releaseId = createResp.releaseId,
          phaseName = phaseName,
          environments = envs,
          redownloadPackages = redownload,
          excludeStepNames = excludes
        )
        logger.message("Deploying release $version …")
        api.parse<DeployResponse>(api.postJson("/release/deploy", deployReq)).deploymentIds
      }
      else -> {
        val promoteReq = PromoteRequest(
          projectId = projectId,
          version = p[JawsDeployRunnerConstants.PARAM_VERSION]?.takeIf { it.isNotBlank() },
          phaseName = phaseName,
          environments = envs,
          redownloadPackages = redownload,
          excludeStepNames = excludes
        )
        logger.message("Promoting ${promoteReq.version ?: "<latest>"} for project $projectId …")
        api.parse<PromoteResponse>(api.postJson("/release/promote", promoteReq)).deploymentIds
      }
    }

    if (deploymentIds.isEmpty()) {
      build.buildLogger.warning("No deployments were created by JawsDeploy API.")
      return
    }

    deploymentIds.forEachIndexed { idx, depId ->
      val flow = build.buildLogger.getFlowLogger("jawsdeploy-${'$'}depId")
      flow.startFlow()
      flow.progressMessage("Tracking deployment ${idx + 1}/${deploymentIds.size}: ${'$'}depId")

      var lastTick: Long? = null
      while (!interrupted) {
        val query = buildString {
          append("/deployment?deploymentId=")
          append(depId)
          if (lastTick != null) append("&getLogsAfter=").append(lastTick)
        }
        val env = api.parse<DeploymentStatusEnvelope>(api.get(query))

        env.logs?.forEach { l ->
          if (l.CreatedUtcTick != null) lastTick = l.CreatedUtcTick
          val text = l.Data ?: ""
          when ((l.LogLevel ?: "").lowercase()) {
            "warning" -> flow.warning(text)
            "error", "critical" -> flow.error(text)
            else -> flow.message(text)
          }
        }

        when (env.status.Status.lowercase()) {
          "queued", "validating", "awaitingslot" -> flow.progressMessage("Deployment queued…")
          "running" -> { /* keep polling */ }
          "completed" -> {
            flow.progressMessage("Deployment completed")
            flow.disposeFlow()
            return@forEachIndexed
          }
          "failed", "cancelled" -> {
            flow.error("Deployment ${'$'}depId ended with status: ${'$'}{env.status.Status}")
            flow.disposeFlow()
            result = BuildFinishedStatus.FINISHED_FAILED
            throw RuntimeException("Deployment failed: ${'$'}depId")
          }
          else -> flow.progressMessage("Status: ${'$'}{env.status.Status}")
        }

        Thread.sleep(pollMs)
      }

      if (interrupted) {
        try {
          flow.warning("Cancellation requested – sending cancel to JawsDeploy…")
          api.postJson("/deployment/cancel", mapOf("deploymentId" to depId))
        } catch (_: Throwable) { }
      }
    }
  }

  override fun makeProgramCommandLine(): ProgramCommandLine {
    // Run a trivial, cross-platform no-op. All real work already ran in beforeProcessStarted().
    val isWindows = System.getProperty("os.name").lowercase().contains("win")
    return if (isWindows)
      createProgramCommandline("cmd", listOf("/c", "echo", "Jaws Deploy done"))
    else
      createProgramCommandline("/bin/sh", listOf("-lc", "printf 'Jaws Deploy done\\n'"))
  }
}