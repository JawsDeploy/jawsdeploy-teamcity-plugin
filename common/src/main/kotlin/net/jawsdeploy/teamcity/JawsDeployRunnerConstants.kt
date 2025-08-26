package net.jawsdeploy.teamcity.shared

object JawsDeployRunnerConstants {
  const val RUN_TYPE = "jawsdeploy"

  // Auth / base
  const val PARAM_API_BASE_URL = "jawsdeploy.apiBaseUrl"
  const val PARAM_LOGIN = "jawsdeploy.login"
  const val PARAM_API_KEY = "secure:jawsdeploy_apiKey"

  // Operation
  const val PARAM_OPERATION = "jawsdeploy.operation" // createAndDeploy | promote

  // Shared
  const val PARAM_PROJECT_ID = "jawsdeploy.projectId"
  const val PARAM_VERSION = "jawsdeploy.version" // default %build.number%
  const val PARAM_PHASE_NAME = "jawsdeploy.phaseName"
  const val PARAM_ENVIRONMENTS = "jawsdeploy.environments" // comma-separated

  // Create options
  const val PARAM_CHANNEL_NAME = "jawsdeploy.channelName"
  const val PARAM_IGNORE_DEFAULT_CHANNEL = "jawsdeploy.ignoreDefaultChannel"
  const val PARAM_NOTES = "jawsdeploy.notes"

  // Deploy/Promote options
  const val PARAM_REDOWNLOAD_PACKAGES = "jawsdeploy.redownloadPackages"
  const val PARAM_EXCLUDE_STEP_NAMES = "jawsdeploy.excludeStepNames" // comma-separated

  // Timeouts / polling (ms)
  const val PARAM_POLL_INTERVAL_MS = "jawsdeploy.pollIntervalMs"
  const val PARAM_REQUEST_TIMEOUT_MS = "jawsdeploy.requestTimeoutMs"
}
