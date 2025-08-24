package com.pine.pinedroid.db

import com.pine.pinedroid.db.bean.DatabaseInfo
import com.pine.pinedroid.utils.appContext

object AppDatabases {
    fun getDatabaseFiles(): List<DatabaseInfo> {
        val databaseList = mutableListOf<DatabaseInfo>()

        // 获取应用的数据库目录
        val databasePath = appContext.getDatabasePath("placeholder").parentFile
        if (databasePath != null && databasePath.exists()) {
            databasePath.listFiles()?.forEach { file ->
                if (file.isFile && (file.name.endsWith(".db") || file.name.endsWith(".db-journal"))) {
                    // 跳过 journal 文件，只处理主数据库文件
                    if (!file.name.endsWith("-journal")) {
                        databaseList.add(
                            DatabaseInfo(
                                name = file.name,
                                path = file.absolutePath,
                                size = file.length(),
                                lastModified = file.lastModified()
                            )
                        )
                    }
                }
            }
        }

//        // 也可以检查 files 目录，有些数据库可能存放在那里
//        val filesDir = appContext.filesDir
//        filesDir.listFiles()?.forEach { file ->
//            if (file.isFile && file.name.endsWith(".db")) {
//                databaseList.add(
//                    DatabaseInfo(
//                        name = file.name,
//                        path = file.absolutePath,
//                        size = file.length(),
//                        lastModified = file.lastModified()
//                    )
//                )
//            }
//        }

        // 按文件名排序
        return databaseList.sortedBy { it.name }
    }
}

