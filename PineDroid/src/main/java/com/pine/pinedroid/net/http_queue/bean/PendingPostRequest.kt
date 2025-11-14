package com.pine.pinedroid.net.http_queue.bean

import com.pine.pinedroid.db.bean.BaseDataTable
import java.util.Date

data class PendingPostRequest (
    override var id: Long? = null,
    var url: String,
    var data: String,
    var is_post: Boolean = false,
    var next_time: Date = Date(),
    var local_files: Map<String, String> = HashMap(),
    var retry_count: Int = 0,
): BaseDataTable() {

}