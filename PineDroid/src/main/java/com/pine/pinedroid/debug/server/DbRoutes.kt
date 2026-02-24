package com.pine.pinedroid.debug.server

import com.pine.pinedroid.db.AppDatabases
import com.pine.pinedroid.db.DbConnection
import io.ktor.http.ContentType
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Routing.dbRoutes() {
    get("/db") {
        val dbs = AppDatabases.getDatabaseFiles()
        val rows = if (dbs.isEmpty()) {
            """<tr><td colspan="3" class="text-muted text-center">No databases found.</td></tr>"""
        } else {
            dbs.joinToString("") { db ->
                val sizeStr = formatDbSize(db.size)
                """<tr>
                   <td><a href="/db/tables?db=${db.name.htmlEscape()}">${db.name.htmlEscape()}</a></td>
                   <td>$sizeStr</td>
                   <td><a href="/db/tables?db=${db.name.htmlEscape()}" class="btn btn-sm btn-outline-primary">Browse</a></td>
                 </tr>"""
            }
        }
        call.respondText(
            page(
                "Database",
                """
                <h5>Databases</h5>
                <table class="table table-sm table-striped bg-white">
                  <thead><tr><th>Name</th><th>Size</th><th></th></tr></thead>
                  <tbody>$rows</tbody>
                </table>
                """
            ),
            ContentType.Text.Html
        )
    }

    get("/db/tables") {
        val dbName = call.request.queryParameters["db"] ?: DbConnection.DEFAULT_DB_NAME
        val safeDb = sanitizeIdentifier(dbName)
        val conn = DbConnection.getInstance(safeDb)
        val tables = try { conn.tables() } catch (e: Exception) { emptyList() }

        val rows = if (tables.isEmpty()) {
            """<tr><td class="text-muted">No tables found.</td></tr>"""
        } else {
            tables.joinToString("") { t ->
                """<tr>
                   <td><a href="/db/data?db=${safeDb.htmlEscape()}&table=${t.name.htmlEscape()}">${t.name.htmlEscape()}</a>
                       <small class="text-muted ms-1">(${t.type})</small></td>
                   <td><a href="/db/data?db=${safeDb.htmlEscape()}&table=${t.name.htmlEscape()}"
                          class="btn btn-sm btn-outline-primary">View</a></td>
                 </tr>"""
            }
        }

        val sqlForm = """
            <form method="post" action="/db/sql" class="mt-4">
              <input type="hidden" name="db" value="${safeDb.htmlEscape()}">
              <div class="mb-2"><label class="form-label fw-bold">Execute SQL</label>
                <textarea name="sql" class="form-control font-monospace" rows="3"
                  placeholder="SELECT * FROM Tour LIMIT 10"></textarea></div>
              <button type="submit" class="btn btn-primary btn-sm">Run</button>
            </form>
        """

        call.respondText(
            page(
                "Tables - $safeDb",
                """
                <div class="d-flex align-items-center gap-2 mb-3">
                  <a href="/db" class="btn btn-sm btn-secondary">&#8592; Databases</a>
                  <h5 class="mb-0">$safeDb</h5>
                </div>
                <table class="table table-sm table-striped bg-white">
                  <thead><tr><th>Table</th><th></th></tr></thead>
                  <tbody>$rows</tbody>
                </table>
                $sqlForm
                """
            ),
            ContentType.Text.Html
        )
    }

    get("/db/data") {
        val dbName = sanitizeIdentifier(call.request.queryParameters["db"] ?: DbConnection.DEFAULT_DB_NAME)
        val tableName = sanitizeIdentifier(call.request.queryParameters["table"] ?: "")
        val page = call.request.queryParameters["page"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0
        val limit = 100
        val offset = page * limit

        if (tableName.isBlank()) {
            call.respondText("Missing table parameter", status = io.ktor.http.HttpStatusCode.BadRequest)
            return@get
        }

        val html = try {
            val conn = DbConnection.getInstance(dbName)
            val (cursor, _) = conn.query(
                "SELECT * FROM $tableName LIMIT $limit OFFSET $offset"
            )
            val colCount = cursor.columnCount
            val colNames = (0 until colCount).map { cursor.getColumnName(it) }
            val rows = mutableListOf<List<String>>()
            while (cursor.moveToNext()) {
                rows.add((0 until colCount).map { i -> cursor.getString(i) ?: "NULL" })
            }
            cursor.close()

            val headerCells = colNames.joinToString("") { "<th>${it.htmlEscape()}</th>" }
            val dataRows = if (rows.isEmpty()) {
                """<tr><td colspan="${colCount}" class="text-center text-muted">No rows</td></tr>"""
            } else {
                rows.joinToString("") { row ->
                    "<tr>" + row.joinToString("") { cell ->
                        """<td class="small">${cell.htmlEscape()}</td>"""
                    } + "</tr>"
                }
            }

            val prevLink = if (page > 0)
                """<a href="/db/data?db=${dbName.htmlEscape()}&table=${tableName.htmlEscape()}&page=${page - 1}"
                   class="btn btn-sm btn-outline-secondary">&#8592; Prev</a>"""
            else ""
            val nextLink = if (rows.size == limit)
                """<a href="/db/data?db=${dbName.htmlEscape()}&table=${tableName.htmlEscape()}&page=${page + 1}"
                   class="btn btn-sm btn-outline-secondary">Next &#8594;</a>"""
            else ""

            page(
                "$tableName - $dbName",
                """
                <div class="d-flex align-items-center gap-2 mb-3">
                  <a href="/db/tables?db=${dbName.htmlEscape()}" class="btn btn-sm btn-secondary">&#8592; Tables</a>
                  <h5 class="mb-0">$tableName</h5>
                  <small class="text-muted">page ${page + 1} (offset $offset)</small>
                </div>
                <div class="table-responsive">
                <table class="table table-sm table-bordered bg-white">
                  <thead class="table-dark"><tr>$headerCells</tr></thead>
                  <tbody>$dataRows</tbody>
                </table>
                </div>
                <div class="d-flex gap-2 mt-2">$prevLink $nextLink</div>
                """
            )
        } catch (e: Exception) {
            page("Error", """<div class="alert alert-danger">${e.message?.htmlEscape()}</div>""")
        }

        call.respondText(html, ContentType.Text.Html)
    }

    post("/db/sql") {
        val params = call.receiveParameters()
        val dbName = sanitizeIdentifier(params["db"] ?: DbConnection.DEFAULT_DB_NAME)
        val sql = params["sql"]?.trim() ?: ""

        val html = if (sql.isBlank()) {
            page("SQL", """<div class="alert alert-warning">No SQL provided.</div>""")
        } else {
            try {
                val conn = DbConnection.getInstance(dbName)
                if (sql.uppercase().trimStart().startsWith("SELECT") ||
                    sql.uppercase().trimStart().startsWith("PRAGMA")
                ) {
                    val (cursor, _) = conn.query(sql)
                    val colCount = cursor.columnCount
                    val colNames = (0 until colCount).map { cursor.getColumnName(it) }
                    val rows = mutableListOf<List<String>>()
                    while (cursor.moveToNext()) {
                        rows.add((0 until colCount).map { i -> cursor.getString(i) ?: "NULL" })
                    }
                    cursor.close()

                    val headerCells = colNames.joinToString("") { "<th>${it.htmlEscape()}</th>" }
                    val dataRows = if (rows.isEmpty()) {
                        """<tr><td colspan="$colCount" class="text-muted text-center">No rows</td></tr>"""
                    } else {
                        rows.joinToString("") { row ->
                            "<tr>" + row.joinToString("") { """<td class="small">${it.htmlEscape()}</td>""" } + "</tr>"
                        }
                    }

                    page(
                        "SQL Result",
                        """
                        <a href="/db/tables?db=${dbName.htmlEscape()}" class="btn btn-sm btn-secondary mb-3">&#8592; Back</a>
                        <div class="alert alert-success small mb-3 font-monospace">${sql.htmlEscape()}</div>
                        <p class="text-muted">${rows.size} row(s)</p>
                        <div class="table-responsive">
                        <table class="table table-sm table-bordered bg-white">
                          <thead class="table-dark"><tr>$headerCells</tr></thead>
                          <tbody>$dataRows</tbody>
                        </table>
                        </div>
                        """
                    )
                } else {
                    conn.execute(sql)
                    page(
                        "SQL Result",
                        """
                        <a href="/db/tables?db=${dbName.htmlEscape()}" class="btn btn-sm btn-secondary mb-3">&#8592; Back</a>
                        <div class="alert alert-success">SQL executed successfully.</div>
                        <pre class="bg-light p-2">${sql.htmlEscape()}</pre>
                        """
                    )
                }
            } catch (e: Exception) {
                page(
                    "SQL Error",
                    """
                    <a href="/db/tables?db=${dbName.htmlEscape()}" class="btn btn-sm btn-secondary mb-3">&#8592; Back</a>
                    <div class="alert alert-danger">${e.message?.htmlEscape()}</div>
                    <pre class="bg-light p-2">${sql.htmlEscape()}</pre>
                    """
                )
            }
        }

        call.respondText(html, ContentType.Text.Html)
    }
}

private fun sanitizeIdentifier(name: String): String =
    name.filter { it.isLetterOrDigit() || it == '_' || it == '.' }

private fun formatDbSize(bytes: Long): String = when {
    bytes < 1024 -> "${bytes}B"
    bytes < 1024 * 1024 -> "${"%.1f".format(bytes / 1024.0)}KB"
    else -> "${"%.1f".format(bytes / (1024.0 * 1024))}MB"
}
