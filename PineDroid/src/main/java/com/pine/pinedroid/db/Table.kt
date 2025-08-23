package com.pine.pinedroid.db


import com.pine.pinedroid.utils.camelToSnakeCase
import com.pine.pinedroid.utils.logv
import java.lang.reflect.ParameterizedType

fun table(name: String, db: String? = null): Table = Table(name, db)

class Table(
    name: String,
    db: String? = null
) {


    private val modelName: String = name
    private val tableName: String = name.camelToSnakeCase()
    private val dbConnection: DbConnection = db(db)

    // 存储列信息
    private val columns: MutableList<ColumnInfo> = mutableListOf()

    fun drop(){
        val sql = "DROP TABLE IF EXISTS $tableName;"
        dbConnection.execute(sql)
    }

    // 添加列（链式）
    fun column(
        columnName: String,
        type: String,
        autoIncrease: Boolean = false,
        primaryKey: Boolean = false,
        notNull: Boolean = false,
        defaultValue: String? = null
    ): Table {
        val existingIndex = columns.indexOfFirst { it.name == columnName }
        val newColumn = ColumnInfo(
            cid = existingIndex.takeIf { it >= 0 } ?: columns.size, // 如果存在，用原 cid，否则新建
            name = columnName,
            type = type,
            notNull = notNull,
            defaultValue = defaultValue,
            isPrimaryKey = primaryKey,
            isAutoIncrement = autoIncrease
        )

        if (existingIndex >= 0) {
            columns[existingIndex] = newColumn  // 替换已有列
        } else {
            columns.add(newColumn)  // 新增列
        }

        return this
    }

    // 生成 SQL 并执行建表
    fun createTable() {
        if (columns.isEmpty()) return
        column("id", "INTEGER", true, true)


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

    companion object{
        fun createTableFromClass(clazz: Class<*>, db: String? = null) {
            val table = table(clazz.simpleName.camelToSnakeCase(), db)

            clazz.declaredFields.forEach { field ->
                // 跳过编译器生成的字段 (一般以 $ 开头)
                if (field.name.startsWith("$")) return@forEach

                val name = field.name
                val type = when (field.type) {
                    Int::class.java, java.lang.Integer::class.java -> "INTEGER"
                    Long::class.java, java.lang.Long::class.java -> "BIGINT"
                    String::class.java -> "TEXT"
                    Boolean::class.java, java.lang.Boolean::class.java -> "INTEGER"
                    Double::class.java, java.lang.Double::class.java -> "REAL"
                    Float::class.java, java.lang.Float::class.java -> "REAL"
                    List::class.java-> "List"
                    else -> null
                }

                if (type == "List") {
                    // 获取 List<T> 的泛型类型
                    val genericType = field.genericType
                    if (genericType is ParameterizedType) {
                        val actualType = genericType.actualTypeArguments[0] as Class<*>
                        createTableFromClass(actualType, db)
                    }
                }
                else if (type != null) {
                    table.column(name, type)
                } else {
                    // 如果是自定义对象，可以创建子表并加外键关联
                    createTableFromClass(field.type, db)
                    // 关联列：foreign_key_id
                    table.column("${name}_id", "INTEGER")
                }
            }
            table.createTable()
        }
    }

}