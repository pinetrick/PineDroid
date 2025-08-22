package com.pine.pinedroid.db

import android.database.Cursor


data class ColumnInfo(
    val cid: Int = 0,
    val name: String = "",
    val type: String = "TEXT",
    val notNull: Boolean = false,
    val defaultValue: String? = null,
    val isPrimaryKey: Boolean = false,
    val isAutoIncrement: Boolean = false
)

class TableStructure private constructor(
    private val dbConnection: DbConnection,
    private val tableName: String
) {

    val columns: MutableList<ColumnInfo> = mutableListOf()

    init {
        val cursor: Cursor = dbConnection.query("PRAGMA table_info($tableName)")
        while (cursor.moveToNext()) {
            val cid = cursor.getInt(cursor.getColumnIndexOrThrow("cid"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val type = cursor.getString(cursor.getColumnIndexOrThrow("type"))
            val notNull = cursor.getInt(cursor.getColumnIndexOrThrow("notnull")) == 1
            val defaultValue = cursor.getString(cursor.getColumnIndexOrThrow("dflt_value"))
            val pk = cursor.getInt(cursor.getColumnIndexOrThrow("pk")) == 1

            val isAutoIncrement = pk && type.uppercase() == "INTEGER"

            columns.add(ColumnInfo(cid, name, type, notNull, defaultValue, pk, isAutoIncrement))
        }
        cursor.close()
    }

    fun getPrimaryKeyColumn(): ColumnInfo? = columns.find { it.isPrimaryKey }

    fun getAutoIncrementColumn(): ColumnInfo? = columns.find { it.isAutoIncrement }

    fun getColumnNames(): List<String> = columns.map { it.name }

    companion object {
        // 缓存 Map：dbConnection -> tableName -> TableStructure
        private val cache = mutableMapOf<DbConnection, MutableMap<String, TableStructure>>()

        fun find(dbConnection: DbConnection, tableName: String): TableStructure {
            val tableMap = cache.getOrPut(dbConnection) { mutableMapOf() }
            return tableMap.getOrPut(tableName) { TableStructure(dbConnection, tableName) }
        }
    }
}
