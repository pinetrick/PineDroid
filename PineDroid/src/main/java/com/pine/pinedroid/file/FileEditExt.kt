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

fun File.isPicture(): Boolean{
    return !isDirectory && (extension.equals("jpg", true) ||
            extension.equals("png", true) ||
            extension.equals("jpeg", true) ||
            extension.equals("webp", true) ||
            extension.equals("svg", true) ||
            extension.equals("gif", true))
}
private val TEXT_EXTENSIONS = setOf(
    // 文本 & 配置
    "txt", "log", "ini", "conf", "cfg", "properties", "env",
    "yaml", "yml", "toml",
    // 数据
    "json", "xml", "html", "htm", "csv", "sql",
    // Web
    "css", "js", "ts", "jsx", "tsx",
    // 代码
    "kt", "java", "py", "rb", "php", "sh", "bash", "bat", "cmd",
    "c", "cpp", "h", "hpp", "go", "rs", "swift", "dart",
    // 文档
    "md", "markdown", "gradle", "gitignore", "gitattributes", "pro"
)

fun File.isTxtFile(): Boolean = !isDirectory && extension.lowercase() in TEXT_EXTENSIONS

/** 对无后缀或未知后缀的文件，通过读取前 512 字节来嗅探是否为纯文本 */
fun File.isLikelyTextFile(maxSize: Long = 5 * 1024 * 1024): Boolean {
    if (isDirectory || !exists() || !canRead()) return false
    if (length() > maxSize) return false
    return try {
        val buf = ByteArray(512)
        val read = inputStream().use { it.read(buf) }
        if (read <= 0) return true
        // 有 null 字节 → 二进制
        if (buf.take(read).any { it == 0.toByte() }) return false
        // 非打印控制字符比例超 10% → 二进制
        val nonPrint = buf.take(read).count { b ->
            val i = b.toInt() and 0xFF
            i < 0x20 && i !in listOf(0x09, 0x0A, 0x0D) // 排除 tab/LF/CR
        }
        nonPrint.toDouble() / read < 0.10
    } catch (_: Exception) {
        false
    }
}