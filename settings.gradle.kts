pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://download.jetbrains.com/teamcity-repository")
  }
}

rootProject.name = "teamcity-jawsdeploy-plugin"
include(":jawsdeploy-server", ":jawsdeploy-agent", ":jawsdeploy-common")