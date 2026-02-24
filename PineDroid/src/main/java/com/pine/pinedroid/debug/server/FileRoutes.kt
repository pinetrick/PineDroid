package com.pine.pinedroid.debug.server

import com.pine.pinedroid.utils.appContext
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.server.request.receiveMultipart
import io.ktor.utils.io.jvm.javaio.toInputStream
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import java.io.File
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date

fun Routing.fileRoutes() {
    get("/files") {
        val path = call.request.queryParameters["path"]

        if (path == null) {
            // Show root listing of allowed base dirs
            val roots = getAllowedRoots()
            val items = roots.joinToString("") { (label, dir) ->
                val enc = enc(dir.absolutePath)
                """<tr><td><a href="/files?path=$enc">&#128193; $label</a></td>
                   <td>${dir.absolutePath.htmlEscape()}</td><td>-</td></tr>"""
            }
            call.respondText(
                page("Files", """
                <h5>File Browser</h5>
                <table class="table table-sm table-striped bg-white">
                  <thead><tr><th>Name</th><th>Path</th><th>Size</th></tr></thead>
                  <tbody>$items</tbody>
                </table>
                """),
                ContentType.Text.Html
            )
            return@get
        }

        val file = resolveFile(path)
        if (file == null) {
            call.respondText("Invalid or unauthorized path", status = HttpStatusCode.BadRequest)
            return@get
        }

        if (!file.exists()) {
            call.respondText("Not found: $path", status = HttpStatusCode.NotFound)
            return@get
        }

        if (file.isDirectory) {
            call.respondText(buildDirHtml(path, file), ContentType.Text.Html)
        } else {
            call.response.headers.append(
                HttpHeaders.ContentDisposition,
                "attachment; filename=\"${file.name}\""
            )
            call.respondBytes(file.readBytes(), ContentType.Application.OctetStream)
        }
    }

    post("/files/upload") {
        val pathParam = call.request.queryParameters["path"] ?: getAllowedRoots().firstOrNull()?.second?.absolutePath ?: ""
        val dir = resolveFile(pathParam)
        if (dir == null || !dir.isDirectory) {
            call.respondText("Invalid directory", status = HttpStatusCode.BadRequest)
            return@post
        }

        var uploaded = 0
        val multipart = call.receiveMultipart()
        var part = multipart.readPart()
        while (part != null) {
            if (part is PartData.FileItem) {
                val fileName = part.originalFileName?.ifBlank { null } ?: "upload_${System.currentTimeMillis()}"
                val outFile = File(dir, fileName)
                outFile.writeBytes(part.provider().toInputStream().readBytes())
                uploaded++
            }
            part.dispose()
            part = multipart.readPart()
        }

        val enc = enc(pathParam)
        call.respondText(
            """<meta http-equiv="refresh" content="0;url=/files?path=$enc">Uploaded $uploaded file(s)...""",
            ContentType.Text.Html
        )
    }

    get("/files/delete") {
        val pathParam = call.request.queryParameters["path"]
        val file = pathParam?.let { resolveFile(it) }
        if (file == null || !file.exists()) {
            call.respondText("Invalid path", status = HttpStatusCode.BadRequest)
            return@get
        }
        file.delete()
        val parentPath = file.parent ?: getAllowedRoots().firstOrNull()?.second?.absolutePath ?: ""
        val enc = enc(parentPath)
        call.respondText(
            """<meta http-equiv="refresh" content="0;url=/files?path=$enc">Deleted.""",
            ContentType.Text.Html
        )
    }
}

private fun buildDirHtml(pathParam: String, dir: File): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
    val parentPath = dir.parent
    val parentLink = if (parentPath != null && resolveFile(parentPath) != null) {
        """<a href="/files?path=${enc(parentPath)}" class="btn btn-sm btn-secondary mb-2">&#8593; Up</a>"""
    } else {
        """<a href="/files" class="btn btn-sm btn-secondary mb-2">&#8593; Up</a>"""
    }

    val files = dir.listFiles()?.sortedWith(compareBy({ !it.isDirectory }, { it.name })) ?: emptyList()
    val rows = files.joinToString("") { f ->
        val name = f.name.htmlEscape()
        val size = if (f.isFile) formatSize(f.length()) else "-"
        val modified = sdf.format(Date(f.lastModified()))
        val nameCell = if (f.isDirectory) {
            """<a href="/files?path=${enc(f.absolutePath)}">&#128193; $name</a>"""
        } else {
            """<a href="/files?path=${enc(f.absolutePath)}">&#128196; $name</a>"""
        }
        val deleteLink = """<a href="/files/delete?path=${enc(f.absolutePath)}"
            onclick="return confirm('Delete $name?')"
            class="btn btn-sm btn-danger py-0">&#10005;</a>"""
        """<tr><td>$nameCell</td><td>$size</td><td>$modified</td><td>$deleteLink</td></tr>"""
    }

    val encPath = enc(pathParam)
    val uploadForm = """
        <form method="post" action="/files/upload?path=$encPath" enctype="multipart/form-data" class="d-flex gap-2 mb-3">
          <input type="file" name="file" class="form-control form-control-sm" style="max-width:300px" multiple>
          <button type="submit" class="btn btn-sm btn-primary">Upload</button>
        </form>
    """

    return page(
        "Files",
        """
        <h6 class="text-muted mb-2">${pathParam.htmlEscape()}</h6>
        $parentLink
        $uploadForm
        <table class="table table-sm table-striped bg-white">
          <thead><tr><th>Name</th><th>Size</th><th>Modified</th><th></th></tr></thead>
          <tbody>$rows</tbody>
        </table>
        """
    )
}

private fun getAllowedRoots(): List<Pair<String, File>> {
    val roots = mutableListOf("Internal" to appContext.filesDir)
    appContext.getExternalFilesDir(null)?.let { roots.add("External" to it) }
    return roots
}

private fun resolveFile(path: String): File? {
    return try {
        val file = File(path).canonicalFile
        val roots = getAllowedRoots().map { it.second.canonicalFile }
        if (roots.any { root -> file.startsWith(root) || file == root }) file else null
    } catch (e: Exception) {
        null
    }
}

private fun enc(path: String) = URLEncoder.encode(path, "UTF-8")

private fun formatSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "${bytes}B"
        bytes < 1024 * 1024 -> "${"%.1f".format(bytes / 1024.0)}KB"
        else -> "${"%.1f".format(bytes / (1024.0 * 1024))}MB"
    }
}
