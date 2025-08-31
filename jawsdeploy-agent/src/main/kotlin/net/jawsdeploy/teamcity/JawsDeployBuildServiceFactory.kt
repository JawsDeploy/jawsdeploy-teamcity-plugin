package net.jawsdeploy.teamcity

import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory
import net.jawsdeploy.teamcity.shared.JawsDeployRunnerConstants
import net.jawsdeploy.teamcity.JawsDeployBuildService

class JawsDeployBuildServiceFactory : CommandLineBuildServiceFactory {
  override fun getBuildRunnerInfo() =
          object : jetbrains.buildServer.agent.AgentBuildRunnerInfo {
            override fun getType() = JawsDeployRunnerConstants.RUN_TYPE
            override fun canRun(config: BuildAgentConfiguration) = true
          }

  override fun createService(): BuildServiceAdapter {
    return JawsDeployBuildService()
  }
}
