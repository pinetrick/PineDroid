package com.pine.pinedroid.debug.server

import com.pine.pinedroid.debug.http_log.HttpLogCollector
import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import java.text.SimpleDateFormat
import java.util.Date

fun Routing.logRoutes() {
    get("/logs") {
        call.respondText(buildLogsHtml(), ContentType.Text.Html)
    }

    get("/logs/clear") {
        HttpLogCollector.clear()
        call.respondText(
            """<meta http-equiv="refresh" content="0;url=/logs">Cleared.""",
            ContentType.Text.Html
        )
    }
}

private fun buildLogsHtml(): String {
    val logs = HttpLogCollector.getLogs()
    val sdf = SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault())

    val rows = if (logs.isEmpty()) {
        """<tr><td colspan="5" class="text-center text-muted">No logs yet. Trigger a network request in the app.</td></tr>"""
    } else {
        logs.joinToString("") { entry ->
            val time = sdf.format(Date(entry.timestamp))
            val rowClass = if (entry.isError) "table-danger" else ""
            val statusBadge = entry.statusCode?.let {
                val cls = when {
                    it >= 500 -> "bg-danger"
                    it >= 400 -> "bg-warning text-dark"
                    it >= 300 -> "bg-info text-dark"
                    else -> "bg-success"
                }
                """<span class="badge $cls">$it</span>"""
            } ?: """<span class="badge bg-secondary">ERR</span>"""

            val methodBadge = when (entry.method) {
                "GET" -> """<span class="badge bg-primary">GET</span>"""
                else -> """<span class="badge bg-warning text-dark">POST</span>"""
            }

            val bodyDetails = buildString {
                if (!entry.requestBody.isNullOrBlank()) {
                    append("""<details><summary class="text-muted small">Request Body</summary>""")
                    append("""<pre class="small bg-light p-1 mt-1" style="max-height:150px;overflow:auto">${entry.requestBody.htmlEscape()}</pre>""")
                    append("</details>")
                }
                if (!entry.responseBody.isNullOrBlank()) {
                    append("""<details><summary class="text-muted small">Response</summary>""")
                    append("""<pre class="small bg-light p-1 mt-1" style="max-height:200px;overflow:auto">${entry.responseBody.htmlEscape()}</pre>""")
                    append("</details>")
                }
            }

            """<tr class="$rowClass">
               <td class="text-nowrap small">$time</td>
               <td>$methodBadge</td>
               <td class="small" style="word-break:break-all">${entry.url.htmlEscape()}</td>
               <td>$statusBadge<br><span class="small text-muted">${entry.durationMs}ms</span></td>
               <td>$bodyDetails</td>
             </tr>"""
        }
    }

    return page(
        "HTTP Logs",
        """
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h5 class="mb-0">HTTP Logs <span class="badge bg-secondary">${logs.size}</span></h5>
          <a href="/logs/clear" class="btn btn-sm btn-outline-danger"
             onclick="return confirm('Clear all logs?')">Clear</a>
        </div>
        <div class="table-responsive">
        <table class="table table-sm table-bordered bg-white">
          <thead class="table-dark">
            <tr><th>Time</th><th>Method</th><th>URL</th><th>Status</th><th>Body</th></tr>
          </thead>
          <tbody>$rows</tbody>
        </table>
        </div>
        """
    )
}
