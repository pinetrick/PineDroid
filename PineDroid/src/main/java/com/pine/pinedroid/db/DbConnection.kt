package com.pine.pinedroid.db

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.pine.pinedroid.db.bean.TableInfo
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
        if (args == null) execSQL(sql)
        else execSQL(sql, args)
    }

    fun query(sql: String, args: Array<Any?>? = null): Pair<Cursor, List<ColumnInfo>> {

        val cursor = if (args == null) rawQuery(sql, null)
        else rawQuery(sql, args.map { it?.toString() }.toTypedArray())

        val columnNames = cursor.columnNames.toList()

        // 创建基本的 ColumnInfo（只有名称和索引）
        val columnInfos = columnNames.mapIndexed { index, name ->
            ColumnInfo(
                cid = index,
                name = name,
                type = "UNKNOWN", // 或者尝试从cursor获取类型信息
                notNull = false,
                defaultValue = null,
                isPrimaryKey = false,
                isAutoIncrement = false
            )
        }

        return Pair(cursor, columnInfos)
    }


    fun insert(tableName: String, nullColumnHack: String?, kvs: MutableMap<String, Any?>): Long {
        // 构建 SQL 日志
        val columns = kvs.keys.joinToString(", ")
        val placeholders = kvs.keys.joinToString(", ") { "?" }
        val sql = "INSERT INTO $tableName \n ($columns) \n VALUES \n ($placeholders)"

        // 构建参数数组用于日志
        val args = kvs.values.map { value ->
            when (value) {
                null -> "NULL"
                is Boolean -> if (value) "1" else "0"
                else -> value.toString()
            }
        }.toTypedArray()

        // 记录 SQL
        logSql(sql, args.map { it as Any? }.toTypedArray())

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
                    is Float -> put(k, v)
                    is Byte -> put(k, v)
                    is Short -> put(k, v)
                    is ByteArray -> put(k, v)
                    else -> put(k, v.toString())
                }
            }
        }

        val result = db.insert(tableName, nullColumnHack, contentValues)

        return result
    }

    /**
     * 获取数据库中的所有表信息
     */
    fun tables(): List<TableInfo> {
        val tables = mutableListOf<TableInfo>()
        var cursor: Cursor? = null

        try {
            // 查询 sqlite_master 表获取所有表信息
            cursor = rawQuery(
                "SELECT name, type, sql FROM sqlite_master WHERE type IN ('table', 'view') AND name NOT LIKE 'sqlite_%' ORDER BY name",
                null
            )

            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val type = cursor.getString(cursor.getColumnIndexOrThrow("type"))
                val sql = cursor.getString(cursor.getColumnIndexOrThrow("sql"))


                tables.add(TableInfo(name, type, sql))
            }
        } catch (e: Exception) {
            logv("DbConnection", "Error getting tables: ${e.message}")
        } finally {
            cursor?.close()
        }

        return tables
    }

    private fun execSQL(sql: String, bindArgs: Array<Any?>? = null) {
        logSql(sql, bindArgs)
        if (bindArgs == null) db.execSQL(sql)
        else db.execSQL(sql, bindArgs)
    }

    private fun rawQuery(sql: String, bindArgs: Array<String?>? = null): Cursor {
        logSql(sql, bindArgs?.map { it as Any? }?.toTypedArray())
        return db.rawQuery(sql, bindArgs)
    }

    private fun logSql(sql: String, bindArgs: Array<Any?>? = null) {
        var tempSql = sql

        bindArgs?.let { args ->
            args.forEach { arg ->
                val value = when {
                    arg == null -> "NULL"
                    arg is Number -> arg.toString()
                    arg is Boolean -> if (arg) "1" else "0"
                    else -> {
                        val str = arg.toString()
                        if (str.toIntOrNull() != null || str.toDoubleOrNull() != null) {
                            str // 看起来像数字的字符串
                        } else {
                            "'${str.replace("'", "''")}'"
                        }
                    }
                }
                tempSql = tempSql.replaceFirst("?", value)
            }

            val formattedSql = formatSqlString(tempSql)
            logv("SQL", formattedSql)
        }
    }

    private fun formatSqlString(sql: String): String {
        return sql
            .replace("SELECT", "\nSELECT\n  ")
            .replace("FROM", "\nFROM\n  ")
            .replace("WHERE", "\nWHERE\n  ")
            .replace("AND", "\nAND\n  ")
            .replace("OR", "\nOR\n  ")
            .replace("ORDER BY", "\nORDER BY\n  ")
            .replace("GROUP BY", "\nGROUP BY\n  ")
            .replace("HAVING", "\nHAVING\n  ")
            .replace("INSERT INTO", "\nINSERT INTO\n  ")
            .replace("VALUES", "\nVALUES\n  ")
            .replace("UPDATE", "\nUPDATE\n  ")
            .replace("SET", "\nSET\n  ")
            .replace("DELETE FROM", "\nDELETE FROM\n  ")
            .replace("JOIN", "\nJOIN\n  ")
            .replace("LEFT JOIN", "\nLEFT JOIN\n  ")
            .replace("RIGHT JOIN", "\nRIGHT JOIN\n  ")
            .replace("INNER JOIN", "\nINNER JOIN\n  ")
            .replace("ON", "\n  ON ")
            .replace("LIMIT", "\nLIMIT ")
            .replace("OFFSET", "\nOFFSET ")
            .trim() + "\n"
    }

    companion object {
        // 缓存所有 dbName 对应的连接
        private val instances = mutableMapOf<String, DbConnection>()

        fun getInstance(dbName: String? = null): DbConnection {
            val name = dbName ?: "app.db"
            return instances.getOrPut(name) { DbConnection(name) }
        }
    }
}
