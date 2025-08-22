package com.pine.pinedroid.db


import com.pine.pinedroid.utils.camelToSnakeCase

fun table(name: String, db: String? = null): Table = Table(name, db)

class Table(name: String, db: String? = null) {

    private val modelName: String = name
    private val tableName: String = name.camelToSnakeCase()
    private val dbConnection: DbConnection = DbConnection(db)

    // 存储列信息
    private val columns: MutableList<ColumnInfo> = mutableListOf()

    // 添加列（链式）
    fun column(
        columnName: String,
        type: String,
        autoIncrease: Boolean = false,
        primaryKey: Boolean = false,
        notNull: Boolean = false,
        defaultValue: String? = null
    ): Table {
        columns.add(
            ColumnInfo(
                cid = columns.size,
                name = columnName,
                type = type,
                notNull = notNull,
                defaultValue = defaultValue,
                isPrimaryKey = primaryKey,
                isAutoIncrement = autoIncrease
            )
        )
        return this
    }

    // 生成 SQL 并执行建表
    fun createTable() {
        if (columns.isEmpty()) return

        val columnsSql = columns.joinToString(", ") { col ->
            val sb = StringBuilder()
            sb.append("${col.name} ${col.type}")
            if (col.isPrimaryKey) sb.append(" PRIMARY KEY")
            if (col.isAutoIncrement) sb.append(" AUTOINCREMENT")
            if (col.notNull) sb.append(" NOT NULL")
            if (col.defaultValue != null) sb.append(
                " DEFAULT '${
                    col.defaultValue.replace(
                        "'",
                        "''"
                    )
                }'"
            )
            sb.toString()
        }

        val sql = "CREATE TABLE IF NOT EXISTS $tableName ($columnsSql);"
        dbConnection.execute(sql)
    }


}