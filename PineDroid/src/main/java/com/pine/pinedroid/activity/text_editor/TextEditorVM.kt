package com.pine.pinedroid.activity.text_editor


import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.activity.db_selection.DbSelectionStatus
import com.pine.pinedroid.db.AppDatabases
import com.pine.pinedroid.db.bean.DatabaseInfo
import com.pine.pinedroid.db.bean.TableInfo
import com.pine.pinedroid.db.db
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import com.pine.pinedroid.utils.file.bToDisplayFileSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.nio.charset.Charset


// TextEditorVM.kt
class TextEditorVM : BaseViewModel<TextEditorState>(TextEditorState::class) {


    private var originalContent: String = ""

    fun loadFile(filePath: String) {
        val file = File(filePath.replace("$", "/"))
        _viewState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (!file.exists()) {
                    _viewState.update {
                        it.copy(
                            error = "文件不存在",
                            isLoading = false
                        )
                    }
                    return@launch
                }

                if (!file.canRead()) {
                    _viewState.update {
                        it.copy(
                            error = "无法读取文件",
                            isLoading = false,
                            isReadOnly = true
                        )
                    }
                    return@launch
                }

                // 检查文件大小（限制大文件编辑）
                if (file.length() > 5 * 1024 * 1024) { // 5MB限制
                    _viewState.update {
                        it.copy(
                            error = "文件过大（${file.length().bToDisplayFileSize()}），无法编辑",
                            isLoading = false,
                            isReadOnly = true
                        )
                    }
                    return@launch
                }

                val content = file.readText(Charset.forName(detectEncoding(file)))
                val canWrite = file.canWrite()

                _viewState.update { viewState ->
                    viewState.copy(
                        filePath = file.absolutePath,
                        fileName = file.name,
                        content = content,
                        isLoading = false,
                        isReadOnly = !canWrite,
                        encoding = detectEncoding(file),
                        lineCount = content.lines().size,
                        wordCount = content.split("\\s+".toRegex()).count { it.isNotBlank() },
                        charCount = content.length,
                        isModified = false
                    )
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

    fun updateContent(newContent: String) {
        _viewState.update { viewState ->
            viewState.copy(
                content = newContent,
                isModified = newContent != originalContent,
                lineCount = newContent.lines().size,
                wordCount = newContent.split("\\s+".toRegex()).count { it.isNotBlank() },
                charCount = newContent.length
            )
        }
    }

    fun saveFile() {
        val state = _viewState.value
        if (state.isReadOnly || state.filePath.isEmpty()) {
            _viewState.update { it.copy(error = "无法保存：文件只读或路径为空") }
            return
        }

        _viewState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = File(state.filePath)
                file.writeText(state.content, Charset.forName(state.encoding))

                _viewState.update {
                    it.copy(
                        isSaving = false,
                        isModified = false,
                    )
                }
            } catch (e: Exception) {
                _viewState.update {
                    it.copy(
                        isSaving = false,
                        error = "保存失败: ${e.message}"
                    )
                }
            }
        }
    }

    fun saveAs(newFile: File) {
        _viewState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                newFile.writeText(_viewState.value.content, Charset.forName(_viewState.value.encoding))

                _viewState.update {
                    it.copy(
                        filePath = newFile.absolutePath,
                        fileName = newFile.name,
                        isSaving = false,
                        isModified = false,
                        isReadOnly = !newFile.canWrite()
                    )
                }
            } catch (e: Exception) {
                _viewState.update {
                    it.copy(
                        isSaving = false,
                        error = "另存为失败: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _viewState.update { it.copy(error = null) }
    }


    private fun detectEncoding(file: File): String {
        // 简单的编码检测，可以根据需要扩展
        return try {
            // 尝试读取为UTF-8
            file.readText(Charset.forName("UTF-8"))
            "UTF-8"
        } catch (e: Exception) {
            try {
                // 尝试读取为GBK（中文环境）
                file.readText(Charset.forName("GBK"))
                "GBK"
            } catch (e: Exception) {
                // 默认使用系统编码
                Charset.defaultCharset().name()
            }
        }
    }
}
