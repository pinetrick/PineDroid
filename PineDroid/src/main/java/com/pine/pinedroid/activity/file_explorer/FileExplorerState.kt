package com.pine.pinedroid.activity.file_explorer

import com.pine.pinedroid.db.bean.DatabaseInfo
import com.pine.pinedroid.utils.shrinker_keep.Keep
import java.io.File
@Keep
data class FileExplorerState(
    val currentDir: String = "/",
    val files: List<File> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showLocationPicker: Boolean = true,
    val locationRoot: String? = null,
)