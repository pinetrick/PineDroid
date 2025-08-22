package com.pine.pinedroid.db

import com.pine.pinedroid.utils.camelToSnakeCase
import com.pine.pinedroid.utils.gson


fun model(name: String, db: String? = null): Model = Model(name, db)

class Model(name: String, db: String? = null) : Iterable<Map<String, Any>> {

    private val modelName: String = name
    private val tableName: String = name.camelToSnakeCase()
    private val dbConnection: DbConnection = DbConnection(db)
    private val tableStructure: TableStructure = TableStructure.find(dbConnection, tableName)
    private val whereConditions = mutableListOf<String>()
    private val whereArgs = mutableListOf<Any?>()

    // 缓存 select 结果
    private var selectResult: List<Map<String, Any>> = emptyList()

    // 当前行数据，用于 insert / update
    private var currentRow: MutableMap<String, Any?> = mutableMapOf()

    private var lastSql = "";

    /** 链式 where 查询 */
    fun <T> where(key: String, value: T?): Model {
        return where(key, "=", value)
    }
    fun <T> where(raw: String): Model {
        whereConditions.add(raw)
        return this
    }
    fun <T> where(key: String, condition: String, value: T?): Model {
        whereConditions.add("$key $condition ?")
        whereArgs.add(value)
        return this
    }

    fun find(id: Int? = null): Model {
        if (id != null) {
            val pkName = tableStructure.getPrimaryKeyColumn() ?: throw Exception("No primary key found")
            where(pkName.name, id)
        }
        select()
        currentRow = selectResult.firstOrNull()?.toMutableMap() ?: mutableMapOf()
        return this
    }

    /** select 查询 */
    fun select(columns: String = "*"): Model {
        val whereSql = if (whereConditions.isEmpty()) "" else "WHERE ${whereConditions.joinToString(" AND ")}"
        lastSql = "SELECT $columns FROM $tableName $whereSql"

        val cursor = dbConnection.query(lastSql, whereArgs.toTypedArray())
        val result = mutableListOf<Map<String, Any>>()
        while (cursor.moveToNext()) {
            val row = mutableMapOf<String, Any>()
            for (i in 0 until cursor.columnCount) {
                row[cursor.getColumnName(i)] = cursor.getString(i)
            }
            result.add(row)
        }
        cursor.close()
        selectResult = result
        return this
    }

    /** 下标操作符 */
    operator fun get(column: String): Any? = currentRow[column]
    operator fun set(column: String, value: Any?) {
        currentRow[column] = value
    }

    fun save(value: Map<String, Any>) {
        currentRow = value.toMutableMap()
        save()
    }

    /** save 方法：主键存在则 update，否则 insert */
    fun save() {
        val pkCol = tableStructure.getPrimaryKeyColumn()
        val pkValue = pkCol?.let { currentRow[it.name] }

        if (pkCol != null && pkValue != null) {
            // UPDATE
            val setSql = currentRow.entries.joinToString(", ") { "${it.key} = ?" }
            lastSql = "UPDATE $tableName SET $setSql WHERE ${pkCol.name} = ?"
            val args = currentRow.values.toMutableList()
            args.add(pkValue)
            dbConnection.execute(lastSql, args.toTypedArray())
        } else {
            // INSERT
            val cols = currentRow.keys.joinToString(", ")
            val placeholders = currentRow.keys.joinToString(", ") { "?" }
            lastSql = "INSERT INTO $tableName ($cols) VALUES ($placeholders)"
            dbConnection.execute(lastSql, currentRow.values.toTypedArray())
        }
        currentRow.clear()
    }

    fun log(): String{
        var r = lastSql + "\n"

        r += if (currentRow.isNotEmpty()) {
            gson.toJson(currentRow)
        } else {
            gson.toJson(selectResult)
        }


        return r;
    }

    /** Iterable 接口实现 */
    override fun iterator(): Iterator<Map<String, Any>> = selectResult.iterator()
}

