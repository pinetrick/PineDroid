package com.pine.pinedroid.activity.text_editor

// TextEditorState.kt
data class TextEditorState(
    val filePath: String = "",
    val fileName: String = "",
    val content: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isModified: Boolean = false,
    val error: String? = null,
    val isReadOnly: Boolean = false,
    val encoding: String = "UTF-8",
    val lineCount: Int = 0,
    val wordCount: Int = 0,
    val charCount: Int = 0
)