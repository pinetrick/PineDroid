package com.pine.pinedroid.file

import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.file.kbToDisplayFileSize
import com.pine.pinedroid.utils.log.logi
import java.io.File


/**
 * 获取文件夹大小
 *
 * @param file
 * File实例
 * @return long 单位为Byte
 * @throws Exception
 */
@Throws(Exception::class)
fun File.getFolderSize(): Long {
    var size: Long = 0
    val fileList = this.listFiles()
    for (i in fileList!!.indices) {
        if (fileList[i]!!.isDirectory()) {
            size = size + fileList[i]!!.getFolderSize()
        } else {
            size = size + fileList[i]!!.length()
        }
    }
    return size
}


/**
 * 获取缓存目录文件大小 返回格式 1.01 MB 2.54 KB
 */
fun getCacheSize(): String? {
    try {
        return appContext.cacheDir.getFolderSize().kbToDisplayFileSize()
    } catch (e: Exception) {
        return "Unknown Size"
    }
}


/**
 * 删除缓存目录
 */
fun deleteCache() {
    try {
        val dir = appContext.cacheDir
        if (dir != null && dir.isDirectory()) {
            dir.deleteDir()
        }
    } catch (e: Exception) {
    }
}


/**
 * 递归删除目录和目录下所有文件
 *
 * <pre>
 *
</pre> *
 *
 * @param dir
 * @return
 */
fun File.deleteDir(): Boolean {
    if (this.isDirectory()) {
        val children = this.list()
        for (i in children!!.indices) {
            val success = File(this, children[i]).deleteDir()
            if (!success) {
                return false
            }
        }
    }
    return this.delete()
}


/**
 * 检测文件是否存在
 *
 * <pre>
 * 输入 : "/mnt/sdcard/a.doc"
</pre> *
 *
 * @param url
 * @return
 */
fun isFileExist(url: String): Boolean {
    val file = File(url)
    return file.exists()
}


/**
 * 创建目录 - 输入请注意格式
 *
 * <pre>
 * "/mnt/a/b.jpg" - 创建 - "/mnt/a"
 * "/mnt/a/b" - 创建 - "/mnt/a"
 * "/mnt/a/b/" - 创建 - "/mnt/a/b"
 * 实现：将删除最后一个反斜杠之后的内容 然后创建
</pre> *
 *
 * @param url
 * @return
 */
fun File.mkDir(url: String): Boolean {
    var url = url
    if (url.contains("/")) {
        url = url.substring(0, url.lastIndexOf("/"))
        logi("创建目录 ： $url")
        val file = File(url)
        file.mkdirs()
        return true
    } else {
        return false
    }
}

