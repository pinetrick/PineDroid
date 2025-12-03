package com.pine.pinedroid.db.bean

import com.pine.pinedroid.db.DbConnection
import com.pine.pinedroid.db.DbRecord
import com.pine.pinedroid.db.model
import com.pine.pinedroid.utils.camelToSnakeCase
import com.pine.pinedroid.utils.shrinker_keep.Keep
import kotlin.reflect.full.memberProperties

@Keep
abstract class BaseDataTable {
    //防止gson 不知道映射哪个字段
    @Transient  // Gson 默认就会排除 transient 字段
    open var id: Long? = null

    @Transient  //如果是true 就执行更新
    open var isExistedRecord: Boolean? = null

    open val _dbName: String
        get() {
            return DbConnection.DEFAULT_DB_NAME
        }

    open val _tableName: String
        get() {
            val className = this::class.simpleName ?: "UnknownTable"
            return className.camelToSnakeCase()
        }

    fun save(forceCreateNewRecord: Boolean = false): BaseDataTable {
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
        if (forceCreateNewRecord) {
            dbRecord.saveNew()
        }
        else {
            dbRecord.save()
        }

        this.id = dbRecord["id"] as Long
        return this
    }

    //先做搜索，确认ID是否存在，如果存在更新，不存在创建
    fun createOrUpdate(): BaseDataTable{
        if (this.id == null) return save()

        val exist = model(_tableName, _dbName).find(id) != null

        return save(!exist)
    }

    fun delete() {
        if (id != null) {
            model(_tableName, _dbName).find(id)?.delete()
        }
    }




}