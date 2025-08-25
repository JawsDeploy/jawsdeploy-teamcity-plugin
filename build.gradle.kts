// Root manages Kotlin version to avoid multi-load warnings
plugins {
  kotlin("jvm") version "1.9.24" apply false
}

allprojects {
  repositories {
    mavenCentral()
    maven("https://download.jetbrains.com/teamcity-repository")
  }
}