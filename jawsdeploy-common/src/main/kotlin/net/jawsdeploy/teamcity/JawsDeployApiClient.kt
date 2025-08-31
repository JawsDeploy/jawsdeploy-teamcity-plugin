package net.jawsdeploy.teamcity.http

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.*

class JawsDeployApiClient(
  baseUrl: String,
  login: String,
  apiKey: String,
  private val requestTimeoutMs: Long
) {
  private val client = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(30))
    .build()

  private val authHeader: String
  private val base: String

  // Visible to public inline functions
  companion object {
    @PublishedApi
    internal val mapper = jacksonObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  }

  init {
    val creds = Base64.getEncoder().encodeToString("$login:$apiKey".toByteArray(Charsets.UTF_8))
    authHeader = "Basic $creds"
    base = baseUrl.trimEnd('/') + "/api"
    require(base.startsWith("http")) { "Invalid JawsDeploy URL: $baseUrl" }
  }

  private fun builder(path: String): HttpRequest.Builder = HttpRequest.newBuilder()
    .uri(URI.create(base + path))
    .header("Authorization", authHeader)

  fun postJson(path: String, body: Any): String {
    val json = mapper.writeValueAsString(body)
    val req = builder(path)
      .timeout(Duration.ofMillis(requestTimeoutMs))
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(json))
      .build()
    val resp = client.send(req, HttpResponse.BodyHandlers.ofString())
    if (resp.statusCode() !in 200..299) throw HttpException("POST $path failed: ${resp.statusCode()} ${resp.body()}")
    return resp.body()
  }

  fun get(pathWithQuery: String): String {
    val req = builder(pathWithQuery)
      .timeout(Duration.ofMillis(requestTimeoutMs))
      .GET().build()
    val resp = client.send(req, HttpResponse.BodyHandlers.ofString())
    if (resp.statusCode() !in 200..299) throw HttpException("GET $pathWithQuery failed: ${resp.statusCode()} ${resp.body()}")
    return resp.body()
  }

  fun <T> parse(json: String, clazz: Class<T>): T = mapper.readValue(json, clazz)
  inline fun <reified T> parse(json: String): T = mapper.readValue(json)
}

class HttpException(msg: String): RuntimeException(msg)