package net.jawsdeploy.teamcity.model

data class CreateReleaseRequest(
  val version: String,
  val projectId: String,
  val channelName: String? = null,
  val ignoreDefaultChannel: Boolean? = null,
  val notes: String? = null,
  val packageVersions: Map<String,String>? = null
)

data class CreateReleaseResponse(val releaseId: String)

data class DeployReleaseRequest(
  val releaseId: String,
  val phaseName: String? = null,
  val environmentName: String? = null,
  val environments: List<String>? = null,
  val redownloadPackages: Boolean? = null,
  val deploymentDateUnixMillis: Long? = null,
  val excludeStepNames: List<String>? = null
)

data class DeployResponse(val deploymentIds: List<String>)

data class PromoteRequest(
  val projectId: String,
  val version: String? = null,
  val phaseName: String? = null,
  val environmentName: String? = null,
  val environments: List<String>? = null,
  val redownloadPackages: Boolean? = null,
  val deploymentDateUnixMillis: Long? = null,
  val excludeStepNames: List<String>? = null
)

data class PromoteResponse(val deploymentIds: List<String>)

data class DeploymentStatusEnvelope(
  val status: DeploymentStatus,
  val logs: List<DeploymentLog>?
)

data class DeploymentStatus(
  val Status: String,
  val ErrorCount: Int?,
  val WarningCount: Int?,
  val LastLogDate: String?,
  val LastLogDateTick: Long?,
  val LastUpdate: String?,
  val CompleteDate: String?
)

data class DeploymentLog(
  val Id: String,
  val CreatedUtc: String?,
  val CreatedUtcTick: Long?,
  val Data: String?,
  val LogLevel: String?,
  val StepId: String?
)