package com.pine.pinedroid.file

import java.io.File

fun File.getFontAwesomeIcon(): String{
    if (isDirectory) return "\uf07b"
    val fileExtName = extension.lowercase()
    return when (fileExtName) {
        "db" -> "\uf1c0"
        "txt", "json" -> "\uf31c"
        "doc", "docx" -> "\uf1c2"
        "xls", "xlsx" -> "\uf1c3"
        "pdf" -> "\uf1c1"
        "zip", "7z", "rar" -> "\uf1c6"
        "wav", "mp3" -> "\uf478"
        "mp4" -> "\uf1c8"
        "jpg", "jpeg", "png", "gif" -> "\uf1c5"
        else -> "\uf15c"
    }

}
fun File.isTxtFile(): Boolean {
    return !isDirectory && (extension.equals("txt", true) ||
            extension.equals("log", true) ||
            extension.equals("xml", true) ||
            extension.equals("json", true) ||
            extension.equals("html", true) ||
            extension.equals("css", true) ||
            extension.equals("js", true) ||
            extension.equals("kt", true) ||
            extension.equals("java", true))
}