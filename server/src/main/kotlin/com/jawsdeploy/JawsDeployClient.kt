package com.jawsdeploy

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64

class JawsDeployClient {
  data class Config(
          val baseUrl: String = "https://app.jawsdeploy.net/api",
          val login: String,
          val apiKey: String
  )
  private val http = HttpClient.newHttpClient()
  private val mapper = jacksonObjectMapper()
  private fun authHeader(cfg: Config): String {
    val token = Base64.getEncoder().encodeToString("${cfg.login}:${cfg.apiKey}".toByteArray())
    return "Basic $token"
  }
  fun createRelease(
          cfg: Config,
          projectId: String,
          version: String,
          channelName: String? = null,
          ignoreDefaultChannel: Boolean? = null,
          notes: String? = null,
          packageVersions: Map<String, String>? = null
  ): String {
    val body =
            mutableMapOf<String, Any>("projectId" to projectId, "version" to version).apply {
              channelName?.let { put("channelName", it) }
              ignoreDefaultChannel?.let { put("ignoreDefaultChannel", it) }
              notes?.let { put("notes", it) }
              packageVersions?.let { put("packageVersions", it) }
            }
    val req =
            HttpRequest.newBuilder()
                    .uri(URI.create("${cfg.baseUrl}/release"))
                    .header("Authorization", authHeader(cfg))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build()
    val resp = http.send(req, HttpResponse.BodyHandlers.ofString())
    if (resp.statusCode() !in 200..299)
            error("Create release failed: ${resp.statusCode()} ${resp.body()}")
    val json: Map<String, Any?> = mapper.readValue(resp.body())
    return json["releaseId"]?.toString() ?: error("releaseId missing")
  }
  fun deployRelease(
          cfg: Config,
          releaseId: String,
          phaseName: String? = null,
          environments: List<String>? = null,
          redownloadPackages: Boolean? = null,
          deploymentDateUnixMillis: Long? = null,
          excludeStepNames: List<String>? = null
  ): List<String> {
    val body =
            mutableMapOf<String, Any>("releaseId" to releaseId).apply {
              phaseName?.let { put("phaseName", it) }
              environments?.let { put("environments", it) }
              redownloadPackages?.let { put("redownloadPackages", it) }
              deploymentDateUnixMillis?.let { put("deploymentDateUnixMillis", it) }
              excludeStepNames?.let { put("excludeStepNames", it) }
            }
    val req =
            HttpRequest.newBuilder()
                    .uri(URI.create("${cfg.baseUrl}/release/deploy"))
                    .header("Authorization", authHeader(cfg))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build()
    val resp = http.send(req, HttpResponse.BodyHandlers.ofString())
    if (resp.statusCode() !in 200..299) error("Deploy failed: ${resp.statusCode()} ${resp.body()}")
    val json: Map<String, Any?> = mapper.readValue(resp.body())
    val ids = (json["deploymentIds"] as? List<*>) ?: emptyList<Any>()
    return ids.map { it.toString() }
  }
  fun promote(
          cfg: Config,
          projectId: String,
          version: String? = null,
          phaseName: String? = null,
          environments: List<String>? = null
  ): List<String> {
    val body =
            mutableMapOf<String, Any>("projectId" to projectId).apply {
              version?.let { put("version", it) }
              phaseName?.let { put("phaseName", it) }
              environments?.let { put("environments", it) }
            }
    val req =
            HttpRequest.newBuilder()
                    .uri(URI.create("${cfg.baseUrl}/release/promote"))
                    .header("Authorization", authHeader(cfg))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build()
    val resp = http.send(req, HttpResponse.BodyHandlers.ofString())
    if (resp.statusCode() !in 200..299) error("Promote failed: ${resp.statusCode()} ${resp.body()}")
    val json: Map<String, Any?> = mapper.readValue(resp.body())
    val ids = (json["deploymentIds"] as? List<*>) ?: emptyList<Any>()
    return ids.map { it.toString() }
  }
}
