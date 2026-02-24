package com.pine.pinedroid.debug.server

import com.pine.pinedroid.utils.prefs
import io.ktor.http.ContentType
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import androidx.core.content.edit

fun Routing.spRoutes() {
    get("/sp") {
        call.respondText(buildSpHtml(), ContentType.Text.Html)
    }

    post("/sp/set") {
        val params = call.receiveParameters()
        val key = params["key"] ?: ""
        val value = params["value"] ?: ""
        if (key.isNotBlank()) {
            prefs.edit { putString(key, value) }
        }
        call.respondText(
            """<meta http-equiv="refresh" content="0;url=/sp">Saved.""",
            ContentType.Text.Html
        )
    }

    get("/sp/delete") {
        val key = call.request.queryParameters["key"] ?: ""
        if (key.isNotBlank()) {
            prefs.edit { remove(key) }
        }
        call.respondText(
            """<meta http-equiv="refresh" content="0;url=/sp">Deleted.""",
            ContentType.Text.Html
        )
    }
}

private fun buildSpHtml(): String {
    val all = prefs.all.entries
        .filter { !it.key.endsWith("_type") }
        .sortedBy { it.key }

    val rows = if (all.isEmpty()) {
        """<tr><td colspan="3" class="text-muted text-center">No preferences found.</td></tr>"""
    } else {
        all.joinToString("") { (key, value) ->
            val displayValue = value?.toString() ?: "null"
            val keyEnc = key.htmlEscape()
            val deleteLink = """<a href="/sp/delete?key=${java.net.URLEncoder.encode(key, "UTF-8")}"
                onclick="return confirm('Delete key: $keyEnc?')"
                class="btn btn-sm btn-danger py-0 px-1">&#10005;</a>"""
            """<tr>
               <td class="small font-monospace">$keyEnc</td>
               <td>
                 <form method="post" action="/sp/set" class="d-flex gap-1">
                   <input type="hidden" name="key" value="$keyEnc">
                   <input type="text" name="value" value="${displayValue.htmlEscape()}"
                     class="form-control form-control-sm" style="min-width:200px">
                   <button type="submit" class="btn btn-sm btn-primary px-2">Save</button>
                 </form>
               </td>
               <td>$deleteLink</td>
             </tr>"""
        }
    }

    val addForm = """
        <div class="card mb-3">
          <div class="card-header fw-bold">Add / Update Key</div>
          <div class="card-body">
            <form method="post" action="/sp/set" class="d-flex gap-2 flex-wrap">
              <input type="text" name="key" placeholder="Key" class="form-control form-control-sm" style="max-width:200px" required>
              <input type="text" name="value" placeholder="Value" class="form-control form-control-sm" style="max-width:300px">
              <button type="submit" class="btn btn-sm btn-success">Add / Save</button>
            </form>
          </div>
        </div>
    """

    return page(
        "SharedPreferences",
        """
        <h5 class="mb-3">SharedPreferences <span class="badge bg-secondary">${all.size}</span></h5>
        $addForm
        <table class="table table-sm table-striped bg-white">
          <thead><tr><th>Key</th><th>Value</th><th></th></tr></thead>
          <tbody>$rows</tbody>
        </table>
        """
    )
}
