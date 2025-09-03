package com.pine.pinedroid.activity.file_explorer

import com.pine.pinedroid.db.bean.DatabaseInfo
import java.io.File

data class FileExplorerState(
    val currentDir: String = "/",
    val files: List<File> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)