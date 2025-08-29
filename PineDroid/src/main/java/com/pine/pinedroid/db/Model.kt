package com.pine.pinedroid.db

import android.database.Cursor
import com.pine.pinedroid.utils.camelToSnakeCase
import kotlin.reflect.KClass


fun model(name: String, db: String? = null): Model = Model(name, db)

open class Model(name: String, private val dbName: String? = null) {

    private val modelName: String = name
    private val tableName: String = name.camelToSnakeCase()

    private data class WhereCondition(
        val condition: String,  // 条件语句，如 "name = ?"
        val operator: String    // 逻辑运算符：AND 或 OR
    )

    private val whereConditions = mutableListOf<WhereCondition>()
    private val whereArgs = mutableListOf<Any?>()

    private var limitCount: Int? = null
    private var orderBy: String = ""
    private var offsetCount: Int? = null

    private var lastSql = "";
    private val tableStructure: TableStructure
        get() = TableStructure.find(dbConnection, tableName)
    private val dbConnection: DbConnection
        get() = db(dbName)
    /** 链式 where 查询 */
// 基础 where 条件（默认 AND）
    fun where(key: String, value: Any?): Model = where(key, "=", value, "AND")

    fun where(key: String, condition: String, value: Any?): Model =
        where(key, condition, value, "AND")

    // 支持指定逻辑运算符的 where
    fun where(key: String, condition: String, value: Any?, operator: String): Model {
        val tmpValue = if (value is Boolean) if (value) 1 else 0 else value
        whereConditions.add(WhereCondition("$key $condition ?", operator))
        whereArgs.add(tmpValue)
        return this
    }

    // 原始 SQL where（默认 AND）
    fun where(raw: String): Model = where(raw, "AND")

    // 支持指定逻辑运算符的原始 SQL where
    fun where(raw: String, operator: String): Model {
        whereConditions.add(WhereCondition(raw, operator))
        return this
    }

    // 专门用于 OR 条件的快捷方法
    fun whereOr(key: String, value: Any?): Model = whereOr(key, "=", value)

    fun whereOr(key: String, condition: String, value: Any?): Model {
        val tmpValue = if (value is Boolean) if (value) 1 else 0 else value
        whereConditions.add(WhereCondition("$key $condition ?", "OR"))
        whereArgs.add(tmpValue)
        return this
    }

    fun whereOr(raw: String): Model {
        whereConditions.add(WhereCondition(raw, "OR"))
        return this
    }

    fun limit(count: Int, offset: Int? = null): Model {
        limitCount = count
        offsetCount = offset
        return this
    }

    fun order(key: String): Model {
        orderBy = "ORDER BY $key"
        return this
    }

    fun count(): Long {
        val list = select("count(*)")
        return list.first()["count(*)"] as Long
    }



    /** 根据主键或条件查找单条记录 */
    fun find(id: Long? = null): DbRecord? {
        if (id != null) {
            val pkCol = tableStructure.getPrimaryKeyColumn()
                ?: throw Exception("No primary key found in $tableName")
            where(pkCol.name, id)
        }

        limit(1)
        val list = select()
        return list.firstOrNull()
    }



    fun select(columns: String = "*"): List<DbRecord> {
        val whereSql = buildWhereClause()

        val limitSql = when {
            limitCount != null && offsetCount != null -> " LIMIT $limitCount OFFSET $offsetCount"
            limitCount != null -> " LIMIT $limitCount"
            else -> ""
        }

        lastSql = "SELECT $columns FROM $tableName $whereSql $orderBy $limitSql"

        return rawQuery(lastSql, whereArgs.toTypedArray()).first

    }


    private fun buildWhereClause(): String {
        if (whereConditions.isEmpty()) return ""

        val whereBuilder = StringBuilder("WHERE ")

        for ((index, condition) in whereConditions.withIndex()) {
            if (index == 0) {
                // 第一个条件不需要逻辑运算符
                whereBuilder.append(condition.condition)
            } else {
                // 后续条件添加逻辑运算符
                whereBuilder.append(" ${condition.operator} ${condition.condition}")
            }
        }

        return whereBuilder.toString()
    }

    fun rawQuery(lastSql: String, args: Array<Any?>? = null): Pair<List<DbRecord>, List<ColumnInfo>> {
        val queryResult = dbConnection.query(lastSql, args)
        val cursor = queryResult.first
        val records = mutableListOf<DbRecord>()

        while (cursor.moveToNext()) {
            val row = mutableMapOf<String, Any?>()
            for (i in 0 until cursor.columnCount) {
                val columnName = cursor.getColumnName(i)
                val columnType = cursor.getType(i)

                row[columnName] = when (columnType) {
                    Cursor.FIELD_TYPE_NULL -> null
                    Cursor.FIELD_TYPE_INTEGER -> cursor.getLong(i)
                    Cursor.FIELD_TYPE_FLOAT -> cursor.getDouble(i)
                    Cursor.FIELD_TYPE_STRING -> cursor.getString(i)
                    Cursor.FIELD_TYPE_BLOB -> cursor.getBlob(i)
                    else -> cursor.getString(i) // 未知类型回退到字符串
                }
            }

            val record = newRecord()
            record.kvs = row
            records.add(record)
        }

        cursor.close()
        return Pair(records, queryResult.second)
    }

    fun newRecord() : DbRecord {
        val record = DbRecord(tableName, dbConnection.dbName)
        return record
    }



}

