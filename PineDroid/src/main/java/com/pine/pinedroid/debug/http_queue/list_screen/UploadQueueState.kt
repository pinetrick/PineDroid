package com.pine.pinedroid.debug.http_queue.list_screen

import com.pine.pinedroid.net.http_queue.bean.PendingPostRequest
import java.util.Date


data class UploadQueueState(
    var queues: List<PendingPostRequest> = sampleRequests

){}

val sampleRequests = listOf(
    PendingPostRequest(
        id = 1,
        url = "https://api.example.com/upload",
        data =  mapOf("key" to "value"),
        is_post = true,
        next_time = Date(),
        local_files = mapOf(
            "image1" to "/storage/emulated/0/Pictures/image1.jpg",
            "image2" to "/storage/emulated/0/Pictures/image2.png"
        ),
        retry_count = 2
    ),
    PendingPostRequest(
        id = 2,
        url = "https://api.example.com/data",
        data = mapOf("key" to "value"),
        is_post = false,
        next_time = Date(System.currentTimeMillis() + 300000), // 5 minutes from now
        local_files = mapOf(),
        retry_count = 0
    )
)