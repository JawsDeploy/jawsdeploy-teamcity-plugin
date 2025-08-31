import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  id("io.github.rodm.teamcity-server") version "1.5"
}

val teamCityVersion = System.getenv("TEAMCITY_VERSION") ?: "2024.12"

dependencies {
  implementation(kotlin("stdlib"))
  compileOnly("org.jetbrains.teamcity:server-api:$teamCityVersion")
  compileOnly("org.jetbrains.teamcity.internal:server:$teamCityVersion")
  compileOnly("javax.servlet:javax.servlet-api:3.1.0")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.+")
  implementation("org.jdom:jdom2:2.0.6.1")

  // Packaged into server ZIP
  server("com.fasterxml.jackson.core:jackson-databind:2.17.+")
  server("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.+")
  server("org.jdom:jdom2:2.0.6.1")

  implementation(project(":jawsdeploy-common"))
}

teamcity {
  version = teamCityVersion
  server {
    archiveName = "teamcity-jawsdeploy-plugin"
    descriptor = file("teamcity-plugin.xml")
    files {
      into("agent") {
        from(project(":jawsdeploy-agent").tasks.named("agentPlugin"))
      }
    }
  }
}

tasks.withType<JavaCompile> { options.release.set(17) }

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "17"
  kotlinOptions.freeCompilerArgs += "-Xjdk-release=17"
}