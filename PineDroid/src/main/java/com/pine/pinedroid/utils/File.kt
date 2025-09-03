package com.pine.pinedroid.utils

import java.io.File


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