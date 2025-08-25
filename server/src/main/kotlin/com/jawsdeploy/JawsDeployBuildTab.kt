package com.jawsdeploy

import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.SimpleCustomTab

class JawsDeployBuildTab(pagePlaces: PagePlaces, descriptor: PluginDescriptor) :
        SimpleCustomTab(
                pagePlaces,
                PlaceId.BUILD_RESULTS_TAB,
                "jawsdeployTab",
                descriptor.getPluginResourcesPath("viewJawsDeployRunnerParams.jsp"),
                "Jaws Deploy"
        ) {
  init {
    register()
  }
}
