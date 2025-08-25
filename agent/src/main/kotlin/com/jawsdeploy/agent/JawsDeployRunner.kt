package com.jawsdeploy.agent

import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.AgentRunningBuild
import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.util.OSType
import java.net.HttpURLConnection
import java.net.URL
import java.net.URI
import java.net.URLEncoder

class JawsDeployRunner : CommandLineBuildServiceFactory {

  override fun getBuildRunnerInfo() = object : jetbrains.buildServer.agent.AgentBuildRunnerInfo {
    override fun getType() = "jawsDeploy"
    override fun canRun(config: BuildAgentConfiguration) = true
  }

  override fun createService(): BuildServiceAdapter = object : BuildServiceAdapter() {
    private var status: BuildFinishedStatus = BuildFinishedStatus.FINISHED_SUCCESS

    override fun beforeProcessStarted() {
      val p = runnerContext.runnerParameters
      val log = build.buildLogger
      try {
        val serverUrl = (p["serverUrl"] ?: build.agentConfiguration.serverUrl).trimEnd('/')
        val action = (p["action"] ?: "promote")
        val path = if (action == "createDeploy") "createDeploy" else "promote"

        fun enc(v: String?) = URLEncoder.encode(v ?: "", Charsets.UTF_8)
        val form = buildString {
          append("login=").append(enc(p["login"]))
          append("&apiKey=").append(enc(p["secure:apiKey"] ?: p["apiKey"]))
          append("&baseUrl=").append(enc(p["baseUrl"]))
          append("&projectId=").append(enc(p["projectId"]))
          if (!p["version"].isNullOrBlank()) append("&version=").append(enc(p["version"]))
          if (!p["environments"].isNullOrBlank()) append("&environments=").append(enc(p["environments"]))
        }

        val uri = URI.create("$serverUrl/jawsDeploy/action.html?mode=$path")
        val conn = (uri.toURL().openConnection() as HttpURLConnection).apply {
          requestMethod = "POST"
          doOutput = true
          setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        }
        conn.outputStream.use { it.write(form.toByteArray(Charsets.UTF_8)) }
        val code = conn.responseCode
        val body = runCatching { conn.inputStream.reader().readText() }
          .getOrElse { conn.errorStream?.reader()?.readText() ?: "" }

        log.message("JawsDeploy: server responded $code")
        if (body.isNotBlank()) log.message(body)
        if (code !in 200..299) status = BuildFinishedStatus.FINISHED_FAILED
      } catch (t: Throwable) {
        build.buildLogger.error("JawsDeploy step failed: ${t.message}")
        status = BuildFinishedStatus.FINISHED_FAILED
      }
    }

    // We don’t actually need to run anything; return a tiny no-op command.
    // Using BuildServiceAdapter helper is the recommended way. :contentReference[oaicite:1]{index=1}
    override fun makeProgramCommandLine(): ProgramCommandLine {
      val isWin = runnerContext.virtualContext.targetOSType == OSType.WINDOWS
      return if (isWin)
        createProgramCommandline("cmd", listOf("/c", "echo", "Jaws Deploy done"))
      else
        createProgramCommandline("/bin/sh", listOf("-lc", "printf 'Jaws Deploy done\\n'"))
    }

    override fun afterProcessFinished() {
      if (status == BuildFinishedStatus.FINISHED_FAILED) {
        throw RunBuildException("JawsDeploy agent step reported failure")
      }
    }
  }
}
