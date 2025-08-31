package net.jawsdeploy.teamcity.http

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.*
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken

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
    val gson: Gson

    init {
        val creds = Base64.getEncoder().encodeToString("$login:$apiKey".toByteArray(Charsets.UTF_8))
        authHeader = "Basic $creds"
        base = baseUrl.trimEnd('/') + "/api"
        require(base.startsWith("http")) { "Invalid JawsDeploy URL: $baseUrl" }
        gson = Gson()
    }

    private fun builder(path: String): HttpRequest.Builder =
        HttpRequest.newBuilder()
            .uri(URI.create(base + path))
            .header("Authorization", authHeader)

    fun _post(path: String, body: Any): String {
        val jsonBody = gson.toJson(body)
        val req = builder(path)
            .timeout(Duration.ofMillis(requestTimeoutMs))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build()
        val resp = client.send(req, HttpResponse.BodyHandlers.ofString())
        if (resp.statusCode() !in 200..299)
            throw HttpException("POST $path failed: ${resp.statusCode()} ${resp.body()}")
        return resp.body()
    }

    fun _get(pathWithQuery: String): String {
        val req = builder(pathWithQuery)
            .timeout(Duration.ofMillis(requestTimeoutMs))
            .GET()
            .build()
        val resp = client.send(req, HttpResponse.BodyHandlers.ofString())
        if (resp.statusCode() !in 200..299)
            throw HttpException("GET $pathWithQuery failed: ${resp.statusCode()} ${resp.body()}")
        return resp.body()
    }

    // --- JSON parsing helpers ---
    inline fun <reified T> parse(json: String): T {
        return when (T::class) {
            String::class -> json as T
            JsonElement::class -> JsonParser.parseString(json) as T
            else -> {
                val type = object : TypeToken<T>() {}.type
                gson.fromJson<T>(json, type)
            }
        }
    }

    inline fun <reified T> get(pathWithQuery: String): T =
        parse(_get(pathWithQuery))

    inline fun <reified T> post(path: String, body: Any): T =
        parse(_post(path, body))
}

class HttpException(msg: String) : RuntimeException(msg)
