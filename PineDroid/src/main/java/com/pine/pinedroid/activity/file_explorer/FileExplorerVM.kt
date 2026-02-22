package com.pine.pinedroid.activity.file_explorer

import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.activity.image_pickup.preview.ImagePreviewScreenVM
import com.pine.pinedroid.db.AppDatabases.isSqliteDatabaseFile
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

class FileExplorerVM : BaseViewModel<FileExplorerState>(FileExplorerState::class) {

    fun initialize() {
        loadDirectory("/data/data/${appContext.packageName}")
    }
    fun loadFile(path: String) {
        val file = File(path)
        if (isSqliteDatabaseFile(file)) {
            navigateTo("table/" + file.name)
        }
        else if (file.isPicture()){
            ImagePreviewScreenVM.images = listOf(OneImage.LocalImage(file.absoluteFile.toString()))
            navigateTo("preview")
        }
        else if (file.isTxtFile()) {
            navigateTo("text_editor/" + file.absoluteFile.toString().replace("/", "$"))
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
        val currentDir = _viewState.value.currentDir
        if (currentDir == "/") {
            currentActivity.finish()
            return
        }

        val parentDir = File(currentDir).parent
        parentDir?.let {
            loadDirectory(it)
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