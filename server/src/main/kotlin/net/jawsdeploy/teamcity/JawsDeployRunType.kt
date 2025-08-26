package net.jawsdeploy.teamcity

import net.jawsdeploy.teamcity.shared.JawsDeployRunnerConstants
import jetbrains.buildServer.serverSide.PropertiesProcessor
import jetbrains.buildServer.serverSide.RunType
import jetbrains.buildServer.serverSide.RunTypeRegistry
import jetbrains.buildServer.web.openapi.PluginDescriptor

class JawsDeployRunType(
  registry: RunTypeRegistry,
  private val pluginDescriptor: PluginDescriptor,
) : RunType() {
  init {
    registry.registerRunType(this)
  }
  override fun getType(): String = JawsDeployRunnerConstants.RUN_TYPE
  override fun getDisplayName(): String = "Jaws Deploy"
  override fun getDescription(): String = "Create/Deploy/Promote releases in Jaws Deploy"

  override fun getRunnerPropertiesProcessor(): PropertiesProcessor = PropertiesProcessor { _ -> emptyList() }
  override fun getEditRunnerParamsJspFilePath(): String = pluginDescriptor.getPluginResourcesPath("editJawsDeployRunnerParams.jsp")
  override fun getViewRunnerParamsJspFilePath(): String = pluginDescriptor.getPluginResourcesPath("viewJawsDeployRunnerParams.jsp")

  override fun getDefaultRunnerProperties(): MutableMap<String, String> = linkedMapOf(
    JawsDeployRunnerConstants.PARAM_OPERATION to "createAndDeploy",
    JawsDeployRunnerConstants.PARAM_VERSION to "%build.number%",
    JawsDeployRunnerConstants.PARAM_POLL_INTERVAL_MS to "2000",
    JawsDeployRunnerConstants.PARAM_REQUEST_TIMEOUT_MS to "120000"
  )
}
