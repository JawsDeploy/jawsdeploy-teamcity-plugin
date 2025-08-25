package com.jawsdeploy

import jetbrains.buildServer.log.Loggers
import jetbrains.buildServer.serverSide.BuildServerAdapter
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.serverSide.SRunningBuild

class JawsDeployServerListener(
        private val server: SBuildServer,
        private val client: JawsDeployClient
) : BuildServerAdapter() {
  init {
    server.addListener(this)
  }
  override fun buildFinished(build: SRunningBuild) {
    val shouldPromote = build.buildOwnParameters["jaws.promote"].equals("true", ignoreCase = true)
    if (!shouldPromote) return
    val login = build.buildOwnParameters["jaws.login"] ?: return
    val apiKey = build.buildOwnParameters["jaws.apikey"] ?: return
    val projectId = build.buildOwnParameters["jaws.projectId"] ?: return
    val envs =
            build.buildOwnParameters["jaws.environments"]
                    ?.split(',', ';')
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }
    val cfg = JawsDeployClient.Config(login = login, apiKey = apiKey)
    try {
      client.promote(cfg, projectId, version = null, environments = envs)
      Loggers.SERVER.info("Jaws: promoted project $projectId")
    } catch (t: Throwable) {
      Loggers.SERVER.warn("Jaws promote failed for $projectId: ${t.message}", t)
    }
  }
}
