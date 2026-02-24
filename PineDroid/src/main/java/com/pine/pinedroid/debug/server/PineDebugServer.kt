package com.pine.pinedroid.debug.server

import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.net.Inet4Address
import java.net.NetworkInterface

object PineDebugServer {
    const val PORT = 8899
    private var server: EmbeddedServer<*, *>? = null

    fun start() {
        if (server != null) return
        try {
            server = embeddedServer(CIO, port = PORT) {
                configureRouting()
            }.start(wait = false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        server?.stop(0, 0)
        server = null
    }

    fun isRunning(): Boolean = server != null

    fun getLocalIpAddress(): String? {
        return try {
            NetworkInterface.getNetworkInterfaces()?.toList()
                ?.flatMap { it.inetAddresses.toList() }
                ?.filterIsInstance<Inet4Address>()
                ?.firstOrNull { !it.isLoopbackAddress }
                ?.hostAddress
        } catch (e: Exception) {
            null
        }
    }

    fun serverUrl(): String? {
        val ip = getLocalIpAddress() ?: return null
        return "http://$ip:$PORT"
    }
}

private fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(
                dashboardHtml(),
                ContentType.Text.Html
            )
        }
        fileRoutes()
        logRoutes()
        dbRoutes()
        spRoutes()
    }
}

private fun dashboardHtml() = page(
    "Dashboard",
    """
    <h4 class="mb-4">Debug Dashboard</h4>
    <div class="row g-3">
      <div class="col-6 col-md-3">
        <a href="/files" class="btn btn-primary btn-lg w-100 py-4">
          <div class="fs-1">&#128193;</div>Files
        </a>
      </div>
      <div class="col-6 col-md-3">
        <a href="/logs" class="btn btn-info btn-lg w-100 py-4 text-white">
          <div class="fs-1">&#128203;</div>HTTP Logs
        </a>
      </div>
      <div class="col-6 col-md-3">
        <a href="/db" class="btn btn-success btn-lg w-100 py-4">
          <div class="fs-1">&#128451;</div>Database
        </a>
      </div>
      <div class="col-6 col-md-3">
        <a href="/sp" class="btn btn-warning btn-lg w-100 py-4">
          <div class="fs-1">&#9881;&#65039;</div>SharedPrefs
        </a>
      </div>
    </div>
    """
)

internal fun page(title: String, content: String) = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <title>$title - Pine Debug</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
</head>
<body class="bg-light">
<nav class="navbar navbar-dark bg-dark mb-3 px-3">
  <a class="navbar-brand fw-bold" href="/">&#128295; Pine Debug</a>
  <div>
    <a href="/files" class="btn btn-sm btn-outline-light me-1">Files</a>
    <a href="/logs" class="btn btn-sm btn-outline-light me-1">Logs</a>
    <a href="/db" class="btn btn-sm btn-outline-light me-1">DB</a>
    <a href="/sp" class="btn btn-sm btn-outline-light">SP</a>
  </div>
</nav>
<div class="container-fluid pb-4">$content</div>
</body>
</html>"""

internal fun String.htmlEscape(): String = this
    .replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")
