package com.pine.pinedroid.debug.http_log

object HttpLogCollector {
    private val logs = ArrayDeque<HttpLogEntry>()
    private const val MAX_SIZE = 200

    @Synchronized
    fun record(entry: HttpLogEntry) {
        if (logs.size >= MAX_SIZE) logs.removeFirst()
        logs.addLast(entry)
    }

    @Synchronized
    fun getLogs(): List<HttpLogEntry> = logs.toList().reversed()

    @Synchronized
    fun clear() = logs.clear()
}

data class HttpLogEntry(
    val timestamp: Long,
    val method: String,
    val url: String,
    val requestBody: String?,
    val statusCode: Int?,
    val responseBody: String?,
    val durationMs: Long,
    val isError: Boolean
)
