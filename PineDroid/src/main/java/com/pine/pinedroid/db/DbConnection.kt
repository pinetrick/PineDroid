package com.pine.pinedroid.db

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.logv
import kotlin.collections.component1
import kotlin.collections.component2

fun db(dbName: String? = null): DbConnection = DbConnection.getInstance(dbName)

class DbConnection private constructor(public var dbName: String) {
    private val db: SQLiteDatabase

    init {
        db = SQLiteDatabase.openOrCreateDatabase(appContext.getDatabasePath(dbName), null)
    }

    fun execute(sql: String, args: Array<Any?>? = null) {
        logv("SQLExecute", sql)
        if (args == null) db.execSQL(sql)
        else db.execSQL(sql, args)
    }

    fun query(sql: String, args: Array<Any?>? = null): Cursor {
        logv("SQLQuery", sql)
        return if (args == null) db.rawQuery(sql, null)
        else db.rawQuery(sql, args.map { it.toString() }.toTypedArray())
    }

    fun insert(tableName: String, nullColumnHack: String?, kvs: MutableMap<String, Any?>): Long {
        logv("SQLInsert", kvs)

        // 使用 insert 方法获取自增ID
        val contentValues = android.content.ContentValues().apply {
            kvs.forEach { (k, v) ->
                when (v) {
                    null -> putNull(k)
                    is Int -> put(k, v)
                    is Long -> put(k, v)
                    is String -> put(k, v)
                    is Boolean -> put(k, if (v) 1 else 0)
                    is Double -> put(k, v)
                    else -> put(k, v.toString())
                }
            }
        }


        return db.insert(tableName, nullColumnHack, contentValues)
    }

    companion object{
        // 缓存所有 dbName 对应的连接
        private val instances = mutableMapOf<String, DbConnection>()

        fun getInstance(dbName: String? = null): DbConnection {
            val name = dbName ?: "app.db"
            return instances.getOrPut(name) { DbConnection(name) }
        }
    }
}
