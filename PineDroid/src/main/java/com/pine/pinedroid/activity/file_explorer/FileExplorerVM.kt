package com.pine.pinedroid.activity.file_explorer

import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.activity.image_pickup.preview.ImagePreviewScreenVM
import com.pine.pinedroid.db.AppDatabases.isSqliteDatabaseFile
import com.pine.pinedroid.file.isLikelyTextFile
import com.pine.pinedroid.file.isPicture
import com.pine.pinedroid.file.isTxtFile

import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.currentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class StorageLocation(val label: String, val path: String, val icon: String)

class FileExplorerVM : BaseViewModel<FileExplorerState>(FileExplorerState::class) {

    fun initialize() {
        _viewState.update { it.copy(showLocationPicker = true) }
    }

    fun getStorageLocations(): List<StorageLocation> {
        val locations = mutableListOf<StorageLocation>()

        locations.add(StorageLocation("应用数据", "/data/data/${appContext.packageName}", "\uf1c0"))

        val externalDirs = appContext.getExternalFilesDirs(null)
        externalDirs.forEachIndexed { index, dir ->
            if (dir != null) {
                var root: File = dir
                repeat(4) { root = root.parentFile ?: root }
                val label = if (index == 0) "内部存储" else "SD卡"
                locations.add(StorageLocation(label, root.absolutePath, "\uf0a0"))
            }
        }

        locations.add(StorageLocation("根目录", "/", "\uf07c"))
        return locations
    }

    fun selectLocation(path: String) {
        _viewState.update { it.copy(showLocationPicker = false, locationRoot = path) }
        loadDirectory(path)
    }
    fun loadFile(path: String) {
        val file = File(path)
        if (isSqliteDatabaseFile(file)) {
            navigateTo("table/" + file.name)
        } else if (file.isPicture()) {
            ImagePreviewScreenVM.images = listOf(OneImage.LocalImage(file.absoluteFile.toString()))
            navigateTo("preview")
        } else if (file.isTxtFile() || file.isLikelyTextFile()) {
            navigateTo("text_editor/" + file.absoluteFile.toString().replace("/", "$"))
        } else {
            openWithSystem(file)
        }
    }

    private fun openWithSystem(file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                appContext,
                "${appContext.packageName}.fileprovider",
                file
            )
            val mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(file.extension.lowercase()) ?: "*/*"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            currentActivity.startActivity(Intent.createChooser(intent, file.name))
        } catch (e: Exception) {
            _viewState.update { it.copy(error = "无法打开文件: ${e.message}") }
        }
    }

    fun loadDirectory(path: String) {
        _viewState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val directory = File(path)
                if (directory.exists() && directory.isDirectory) {
                    val fileList = directory.listFiles()?.sortedWith(compareBy(
                        { !it.isDirectory }, // 文件夹在前
                        { it.name.lowercase() } // 按名称排序
                    )) ?: emptyList()

                    _viewState.update {
                        it.copy(
                            currentDir = path,
                            files = fileList,
                            isLoading = false
                        )
                    }
                } else {
                    _viewState.update {
                        it.copy(
                            error = "目录不存在或无法访问",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _viewState.update {
                    it.copy(
                        error = "加载失败: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun navigateToParent() {
        val state = _viewState.value
        if (state.showLocationPicker) {
            currentActivity.finish()
            return
        }

        val currentDir = state.currentDir
        if (currentDir == state.locationRoot || File(currentDir).parent == null) {
            _viewState.update { it.copy(showLocationPicker = true) }
            return
        }

        val parentDir = File(currentDir).parent
        if (parentDir != null) {
            loadDirectory(parentDir)
        } else {
            _viewState.update { it.copy(showLocationPicker = true) }
        }
    }

    fun onDatabase() {
        navigateTo("db")
    }

    fun refresh() {
        loadDirectory(_viewState.value.currentDir)
    }

    fun deleteFile(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = if (file.isDirectory) file.deleteRecursively() else file.delete()
                if (success) refresh()
                else _viewState.update { it.copy(error = "删除失败") }
            } catch (e: Exception) {
                _viewState.update { it.copy(error = "删除失败: ${e.message}") }
            }
        }
    }

    fun createFile(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = File(_viewState.value.currentDir, name)
                if (file.createNewFile()) refresh()
                else _viewState.update { it.copy(error = "创建失败，文件可能已存在") }
            } catch (e: Exception) {
                _viewState.update { it.copy(error = "创建失败: ${e.message}") }
            }
        }
    }

    fun createFolder(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dir = File(_viewState.value.currentDir, name)
                if (dir.mkdir()) refresh()
                else _viewState.update { it.copy(error = "创建失败，文件夹可能已存在") }
            } catch (e: Exception) {
                _viewState.update { it.copy(error = "创建失败: ${e.message}") }
            }
        }
    }
}