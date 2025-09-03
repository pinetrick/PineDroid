package com.pine.pinedroid.db

import com.pine.pinedroid.db.bean.DatabaseInfo
import com.pine.pinedroid.utils.appContext
import java.io.File
import java.io.FileInputStream

object AppDatabases {
    fun getDatabaseFiles(): List<DatabaseInfo> {
        val databaseList = mutableListOf<DatabaseInfo>()
        val processedNames = mutableSetOf<String>()

        // 获取应用的数据库目录
        val databasePath = appContext.getDatabasePath("placeholder").parentFile
        if (databasePath != null && databasePath.exists()) {
            databasePath.listFiles()?.forEach { file ->
                if (file.isFile) {
                    val fileName = file.name

                    // 跳过临时文件（journal, shm, wal）
                    if (fileName.endsWith("-journal") ||
                        fileName.endsWith("-shm") ||
                        fileName.endsWith("-wal") ||
                        fileName.endsWith(".db-journal")) {
                        return@forEach
                    }

                    // 检查常见数据库扩展名
                    val isDbExtension = fileName.endsWith(".db") ||
                            fileName.endsWith(".sqlite") ||
                            fileName.endsWith(".sqlite3") ||
                            fileName.endsWith(".db3")

                    // 或者通过文件头检查（更准确但需要读取文件）
                    val isLikelyDatabase = isDbExtension || isSqliteDatabaseFile(file)

                    if (isLikelyDatabase) {
                        // 获取主数据库文件名（去除可能的临时后缀）
                        val baseName = getBaseDatabaseName(fileName)

                        // 避免重复添加同一个数据库的不同文件
                        if (baseName !in processedNames) {
                            processedNames.add(baseName)

                            // 计算总大小（包括相关的临时文件）
                            val totalSize = calculateTotalDatabaseSize(databasePath, baseName)

                            databaseList.add(
                                DatabaseInfo(
                                    name = baseName,
                                    path = file.absolutePath,
                                    size = totalSize,
                                    lastModified = file.lastModified()
                                )
                            )
                        }
                    }
                }
            }
        }

        // 按文件名排序
        return databaseList.sortedBy { it.name }
    }

    /**
     * 检查文件是否是 SQLite 数据库文件（通过文件头）
     */
    fun isSqliteDatabaseFile(file: File): Boolean {
        return try {
            FileInputStream(file).use { stream ->
                val header = ByteArray(16)
                if (stream.read(header) == 16) {
                    val headerString = String(header, Charsets.US_ASCII)
                    headerString.startsWith("SQLite format 3")
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 获取数据库的基础名称（去除临时文件后缀）
     */
    private fun getBaseDatabaseName(fileName: String): String {
        return fileName.removeSuffix("-journal")
            .removeSuffix("-shm")
            .removeSuffix("-wal")
            .removeSuffix(".db-journal")
    }

    /**
     * 计算数据库的总大小（包括主文件和所有相关临时文件）
     */
    private fun calculateTotalDatabaseSize(directory: File, baseName: String): Long {
        var totalSize = 0L

        // 可能的相关文件
        val possibleFiles = listOf(
            baseName,
            "$baseName-journal",
            "$baseName-shm",
            "$baseName-wal",
            "$baseName.db-journal"
        )

        possibleFiles.forEach { fileName ->
            val file = File(directory, fileName)
            if (file.exists() && file.isFile) {
                totalSize += file.length()
            }
        }

        return totalSize
    }


}

