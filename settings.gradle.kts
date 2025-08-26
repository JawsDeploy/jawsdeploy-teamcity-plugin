pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://download.jetbrains.com/teamcity-repository")
  }
}

rootProject.name = "teamcity-jawsdeploy-plugin"
include(":server", ":agent", ":common")