package com.jawsdeploy

import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor
import jetbrains.buildServer.serverSide.RunType
import jetbrains.buildServer.serverSide.RunTypeRegistry
import jetbrains.buildServer.serverSide.WebLinks
import jetbrains.buildServer.web.openapi.PluginDescriptor

class JawsDeployRunType(
        registry: RunTypeRegistry,
        private val descriptor: PluginDescriptor,
        private val weblinks: WebLinks
) : RunType() {
        init {
                registry.registerRunType(this)
        }
        override fun getType() = "jawsDeploy"
        override fun getDisplayName() = "Jaws Deploy"
        override fun getDescription() = "Create/Deploy/Promote releases via Jaws Deploy"

        override fun getEditRunnerParamsJspFilePath() =
                descriptor.getPluginResourcesPath("editJawsDeployRunnerParams.jsp")

        override fun getViewRunnerParamsJspFilePath() =
                descriptor.getPluginResourcesPath("viewJawsDeployRunnerParams.jsp")

        override fun getRunnerPropertiesProcessor() = PropertiesProcessor { props ->
                val errors = mutableListOf<InvalidProperty>()
                fun need(k: String) {
                        if (props[k].isNullOrBlank()) errors += InvalidProperty(k, "Required")
                }
                need("action")
                need("login")
                need("secure:apiKey")
                need("projectId")
                need("environments")
                errors
        }

        override fun getDefaultRunnerProperties() =
                mutableMapOf("action" to "promote", "baseUrl" to "https://app.jawsdeploy.net/api")
}
