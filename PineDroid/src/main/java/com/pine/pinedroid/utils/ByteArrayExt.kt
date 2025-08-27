package com.pine.pinedroid.utils

fun ByteArray.ToString(maxLines: Int = 10, bytesPerLine: Int = 32): String {
    if (this.isEmpty()) return "Empty ByteArray"


    val lines = this.toList().chunked(bytesPerLine)

    val result = lines.take(maxLines).joinToString("\n") { line ->
        line.joinToString(" ") { byte -> "%02X".format(byte) }
    }

    return if (lines.size > maxLines) {
        "$result\n... (${this.size - maxLines * bytesPerLine} more bytes)"
    } else {
        result
    }
}