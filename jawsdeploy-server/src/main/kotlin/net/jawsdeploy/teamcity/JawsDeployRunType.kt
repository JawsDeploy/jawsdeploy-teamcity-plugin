package net.jawsdeploy.teamcity

import net.jawsdeploy.teamcity.shared.JawsDeployRunnerConstants
import jetbrains.buildServer.serverSide.InvalidProperty
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

  override fun getRunnerPropertiesProcessor(): PropertiesProcessor = PropertiesProcessor { props ->
        val errors = mutableListOf<InvalidProperty>()

        val url = props[JawsDeployRunnerConstants.PARAM_API_BASE_URL]?.trim().orEmpty()
        if (url.isEmpty()) {
            errors += InvalidProperty(JawsDeployRunnerConstants.PARAM_API_BASE_URL, "Server URL is required")
        } else if (!url.startsWith("http://") && !url.startsWith("https://")) {
            errors += InvalidProperty(JawsDeployRunnerConstants.PARAM_API_BASE_URL, "Server URL must start with http:// or https://")
        }

        val projectId = props[JawsDeployRunnerConstants.PARAM_PROJECT_ID]?.trim().orEmpty()
        if (projectId.isEmpty()) {
            errors += InvalidProperty(JawsDeployRunnerConstants.PARAM_PROJECT_ID, "Project ID is required")
        }

        val login = props[JawsDeployRunnerConstants.PARAM_LOGIN]?.trim().orEmpty()
        if (login.isEmpty()) {
            errors += InvalidProperty(JawsDeployRunnerConstants.PARAM_LOGIN, "Login is required")
        }

        val apiKey = props[JawsDeployRunnerConstants.PARAM_API_KEY]?.trim().orEmpty()
        if (apiKey.isEmpty()) {
            errors += InvalidProperty(JawsDeployRunnerConstants.PARAM_API_KEY, "Password / API Key is required")
        }

        val envs = props[JawsDeployRunnerConstants.PARAM_ENVIRONMENTS]?.trim().orEmpty()
        if (envs.isEmpty()) {
            errors += InvalidProperty(JawsDeployRunnerConstants.PARAM_ENVIRONMENTS, "Environment is required")
        }

        errors
    }

  override fun getEditRunnerParamsJspFilePath(): String = pluginDescriptor.getPluginResourcesPath("editJawsDeployRunnerParams.jsp")
  override fun getViewRunnerParamsJspFilePath(): String = pluginDescriptor.getPluginResourcesPath("viewJawsDeployRunnerParams.jsp")

  override fun getDefaultRunnerProperties(): MutableMap<String, String> = linkedMapOf(
    JawsDeployRunnerConstants.PARAM_API_BASE_URL to "https://app.jawsdeploy.net",
    JawsDeployRunnerConstants.PARAM_OPERATION to "createAndDeploy",
    JawsDeployRunnerConstants.PARAM_VERSION to "%build.number%",
    JawsDeployRunnerConstants.PARAM_POLL_INTERVAL_MS to "2000",
    JawsDeployRunnerConstants.PARAM_REQUEST_TIMEOUT_MS to "120000"
  )
}
