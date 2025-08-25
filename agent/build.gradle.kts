import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  id("io.github.rodm.teamcity-agent") version "1.5"
}

val teamCityVersion = System.getenv("TEAMCITY_VERSION") ?: "2024.12"

repositories {
  mavenCentral()
  maven("https://download.jetbrains.com/teamcity-repository")
}

dependencies {
  // put API on the *compile* classpath (not packaged)
  compileOnly("org.jetbrains.teamcity:agent-api:$teamCityVersion")
  implementation(kotlin("stdlib"))
}

tasks.withType<JavaCompile> { options.release.set(17) }
tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "17"
  kotlinOptions.freeCompilerArgs += "-Xjdk-release=17"
}

teamcity {
  version = teamCityVersion
  agent {
    // we package this agent jar via :server
    descriptor = file("../server/teamcity-plugin.xml")
    files { from("src/main/resources") }
  }
}
