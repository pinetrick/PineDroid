package com.pine.pinedroid.db.bean

import com.pine.pinedroid.db.DbConnection
import com.pine.pinedroid.db.DbRecord
import com.pine.pinedroid.db.model
import com.pine.pinedroid.utils.camelToSnakeCase
import kotlin.reflect.full.memberProperties

abstract class BaseDataTable  {
    open var id: Long? = null
    open var _dbName: String = DbConnection.DEFAULT_DB_NAME
    open val _tableName: String by lazy {
        val className = this::class.simpleName ?: "UnknownTable"
        className.camelToSnakeCase()
    }

    fun save(): BaseDataTable {
        val dbRecord = DbRecord(_tableName, _dbName)

        // 获取当前对象的所有属性
        this::class.memberProperties.forEach { property ->
            val fieldName = property.name

            // 跳过编译器生成的字段和以下划线开头的字段
            if (fieldName.startsWith("$") || fieldName.startsWith("_")) {
                return@forEach
            }

            try {
                // 获取属性的值
                val value = property.getter.call(this)

                // 设置到 DbRecord 中
                dbRecord[fieldName] = value
            } catch (e: Exception) {
                // 处理获取属性值时的异常
                println("Warning: Could not get value for property $fieldName: ${e.message}")
            }
        }

        dbRecord.save()
        this.id = dbRecord["id"] as Long
        return this
    }

    fun delete() {
        if (id != null) {
            model(_tableName, _dbName).find(id)?.delete()
        }
    }


}