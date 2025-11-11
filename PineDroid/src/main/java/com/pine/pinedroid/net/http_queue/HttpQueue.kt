package com.pine.pinedroid.net.http_queue

import com.pine.pinedroid.db.model
import com.pine.pinedroid.db.table
import com.pine.pinedroid.net.httpGet
import com.pine.pinedroid.net.httpPostJson
import com.pine.pinedroid.net.http_queue.bean.PendingPostRequest
import com.pine.pinedroid.utils.gson
import com.pine.pinedroid.utils.log.logd
import com.pine.pinedroid.utils.log.loge
import kotlinx.coroutines.*
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean

fun asyncHttpGet(url: String) {
    PendingPostRequest(
        url = url,
        data = "",
        is_post = false,
    ).save()
    HttpQueue.i.tryStartProcessing()
}

fun asyncHttpPost(url: String, data: Any? = null) {
    PendingPostRequest(
        url = url,
        data = gson.toJson(data),
        is_post = true,
    ).save()
    HttpQueue.i.tryStartProcessing()
}

class HttpQueue private constructor() {

    private var processingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val isProcessing = AtomicBoolean(false)
    private val processingLock = Any()

    init {
        table<PendingPostRequest>().createTable()
    }

    /**
     * 尝试启动队列处理（按需启动）
     */
    internal fun tryStartProcessing() {
        synchronized(processingLock) {
            if (isProcessing.get() && processingJob?.isActive == true) {
                return
            }

            isProcessing.set(true)
            startQueueProcessing()
        }
    }

    private fun startQueueProcessing() {
        // 取消之前的任务
        processingJob?.cancel()

        processingJob = scope.launch {
            logd("HttpQueue processing started")

            while (isActive && isProcessing.get()) {
                try {
                    val hasMore = handleQueue()

                    if (!hasMore) {
                        // 没有更多待处理请求，停止处理循环
                        logd("No more pending requests, stopping queue processing")
                        stopProcessing()
                        break
                    }

                    // 每次处理完后等待一段时间再继续
                    delay(30_000) // 30秒间隔
                } catch (e: CancellationException) {
                    // 正常取消，不记录错误
                    break
                } catch (e: Exception) {
                    loge("Error in queue processing: ${e.message}")
                    delay(60_000) // 出错时等待1分钟
                }
            }
        }
    }

    /**
     * 处理队列，返回是否还有更多请求需要处理
     */
    private suspend fun handleQueue(): Boolean {
        logd("Handle Http Queue")
        var hasMore = false

        do {
            val pendingPostRequest: PendingPostRequest? = withContext(Dispatchers.IO) {
                model<PendingPostRequest>()
                    .where("next_time", "<=", Date())
                    .order("next_time")
                    .find()
            }

            if (pendingPostRequest == null) {
                // 没有待处理请求
                return hasMore
            }

            handlePendingPostRequest(pendingPostRequest)
            hasMore = true

            // 短暂延迟，避免过于密集的处理
            delay(100)

        } while (true)
    }

    private suspend fun handlePendingPostRequest(pendingPostRequest: PendingPostRequest) {
        try {
            val success = if (pendingPostRequest.is_post) {
                val resp = httpPostJson<String>(pendingPostRequest.url, pendingPostRequest.data)
                resp != null
            } else {
                val resp = httpGet<String>(pendingPostRequest.url)
                resp != null
            }

            if (success) {
                pendingPostRequest.delete()
                logd("Successfully processed request for URL: ${pendingPostRequest.url}")
            } else {
                handleFailedRequest(pendingPostRequest)
            }
        } catch (e: Exception) {
            loge("Exception handling request for URL: ${pendingPostRequest.url}, error: ${e.message}")
            handleFailedRequest(pendingPostRequest)
        }
    }

    private suspend fun handleFailedRequest(pendingPostRequest: PendingPostRequest) {
        val retryCount = pendingPostRequest.retry_count.plus(1)

        // 检查是否超过最大重试次数
        if (retryCount > MAX_RETRY_COUNT) {
            loge("Request exceeded max retry count ($MAX_RETRY_COUNT) for URL: ${pendingPostRequest.url}, deleting")
            pendingPostRequest.delete()
            return
        }

        val backoffDelay = calculateBackoffDelay(retryCount)
        val nextTime = Date(System.currentTimeMillis() + backoffDelay)

        pendingPostRequest.next_time = nextTime
        pendingPostRequest.retry_count = retryCount
        pendingPostRequest.save()

        loge("Failed to process request for URL: ${pendingPostRequest.url}, retry count: $retryCount, next retry: $nextTime")
    }

    private fun calculateBackoffDelay(retryCount: Int): Long {
        // 指数退避策略：2^retryCount * 基础延迟时间
        val baseDelay = 5 * 60 * 1000L // 5分钟基础延迟
        val maxDelay = 24 * 60 * 60 * 1000L // 最大延迟24小时

        // 防止位移操作溢出
        return if (retryCount <= 30) {
            minOf(baseDelay * (1L shl (retryCount - 1)), maxDelay)
        } else {
            maxDelay
        }
    }

    /**
     * 停止处理（不取消scope）
     */
    private fun stopProcessing() {
        synchronized(processingLock) {
            isProcessing.set(false)
            processingJob?.cancel()
            processingJob = null
        }
    }

    /**
     * 完全停止队列处理
     */
    fun stop() {
        stopProcessing()
        scope.cancel()
    }

    /**
     * 重新启动队列处理
     */
    fun restart() {
        synchronized(processingLock) {
            isProcessing.set(true)
            startQueueProcessing()
        }
    }

    /**
     * 获取队列状态
     */
    suspend fun getQueueStatus(): QueueStatus {
        return withContext(Dispatchers.IO) {
            val totalCount = model<PendingPostRequest>().count()
            val pendingCount = model<PendingPostRequest>()
                .where("next_time", "<=", Date())
                .count()
            val nextRetryTime = model<PendingPostRequest>()
                .where("next_time", ">", Date())
                .order("next_time")
                .find()
                ?.next_time

            QueueStatus(totalCount, pendingCount, nextRetryTime)
        }
    }

    /**
     * 清空所有待处理请求
     */
    suspend fun clearAll(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                model<PendingPostRequest>().truncate()

                true
            } catch (e: Exception) {
                loge("Error clearing queue: ${e.message}")
                false
            }
        }
    }

    /**
     * 清理过期的失败请求（超过最大重试次数的）
     */
    suspend fun cleanupExpiredRequests(): Int {
        return withContext(Dispatchers.IO) {
            try {
                val expiredRequests = model<PendingPostRequest>()
                    .where("retry_count", ">", MAX_RETRY_COUNT)
                    .select()

                val count = expiredRequests.size
                expiredRequests.forEach { it.delete() }
                count
            } catch (e: Exception) {
                loge("Error cleaning up expired requests: ${e.message}")
                0
            }
        }
    }

    data class QueueStatus(
        val totalRequests: Long,
        val pendingRequests: Long,
        val nextRetryTime: Date? = null
    )

    companion object {
        private const val MAX_RETRY_COUNT = 10 // 最大重试次数

        val i by lazy { HttpQueue() }
    }
}