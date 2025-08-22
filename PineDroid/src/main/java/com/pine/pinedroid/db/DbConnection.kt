package com.pine.pinedroid.db

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.pine.pinedroid.utils.appContext

class DbConnection(_dbName: String? = null) {
    private val db: SQLiteDatabase

    var dbName: String = "" //"app.db"

    init {
        dbName = _dbName ?: "app.db"
        db = SQLiteDatabase.openOrCreateDatabase(appContext.getDatabasePath(dbName), null)
    }

    fun execute(sql: String, args: Array<Any?>? = null) {
        if (args == null) db.execSQL(sql)
        else db.execSQL(sql, args)
    }

    fun query(sql: String, args: Array<Any?>? = null): Cursor {
        return if (args == null) db.rawQuery(sql, null)
        else db.rawQuery(sql, args.map { it.toString() }.toTypedArray())
    }
}
