package com.pine.pinedroid.db

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.logv

fun db(dbName: String? = null): DbConnection = DbConnection.getInstance(dbName)

class DbConnection private constructor(public var dbName: String) {
    private val db: SQLiteDatabase

    init {
        db = SQLiteDatabase.openOrCreateDatabase(appContext.getDatabasePath(dbName), null)
    }

    fun execute(sql: String, args: Array<Any?>? = null) {
        logv("SQL", sql)
        if (args == null) db.execSQL(sql)
        else db.execSQL(sql, args)
    }

    fun query(sql: String, args: Array<Any?>? = null): Cursor {
        return if (args == null) db.rawQuery(sql, null)
        else db.rawQuery(sql, args.map { it.toString() }.toTypedArray())
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
