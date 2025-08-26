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
  compileOnly("org.jetbrains.teamcity:agent-api:$teamCityVersion")
  implementation(kotlin("stdlib"))
  implementation(project(":common"))
}

tasks.withType<JavaCompile> { options.release.set(17) }
tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "17"
  kotlinOptions.freeCompilerArgs += "-Xjdk-release=17"
}
