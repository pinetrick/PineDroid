package com.pine.pinedroid.db

import com.pine.pinedroid.utils.camelToSnakeCase
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType

inline fun <reified T : Any> model(db: String? = null): ModelK<T> = ModelK(T::class, db)

class ModelK<T : Any>(kclass: KClass<T>, dbName: String? = null) {

    private val kclass: KClass<T> = kclass
    private val constructor = kclass.constructors.first()
    private val parameters = constructor.parameters

    private val model = Model(kclass.simpleName!!, dbName)

    /**
     * 根据主键或条件查找单条记录，并转换为指定类型
     */
    fun find(id: Int? = null): T? {
        val dbRecord = model.find(id) ?: return null
        return convertToType(dbRecord)
    }

    /**
     * 查询多条记录并转换为指定类型列表
     */
    fun where(key: String, value: Any?): ModelK<T> {
        model.where(key, value)
        return this
    }

    fun where(raw: String): ModelK<T> {
        model.where(raw)
        return this
    }
    fun where(key: String, condition: String, value: Any?): ModelK<T> {
        model.where(key, condition, value)
        return this
    }
    fun limit(count: Int, offset: Int? = null): ModelK<T> {
        model.limit(count, offset)
        return this
    }

    /**
     * 将 DbRecord 转换为指定类型 T
     */
    private fun convertToType(dbRecord: DbRecord): T? {
        return try {
            val args = parameters.associateWith { param ->
                val paramName = param.name ?: ""
                val dbValue = findValueInRecord(dbRecord, paramName)
                convertValue(dbValue, param.type)
            }
            constructor.callBy(args)
        } catch (e: Exception) {
            println("Error converting DbRecord to ${kclass.simpleName}: ${e.message}")
            null
        }
    }

    /**
     * 在 DbRecord 中查找值（支持驼峰和下划线命名）
     */
    private fun findValueInRecord(dbRecord: DbRecord, paramName: String): Any? {
        return dbRecord.kvs[paramName] ?:
        dbRecord.kvs[paramName.camelToSnakeCase()] ?:
        dbRecord.kvs[paramName.uppercase()] ?:
        dbRecord.kvs[paramName.lowercase()]
    }

    /**
     * 值类型转换
     */
    private fun convertValue(value: Any?, targetType: KType): Any? {
        if (value == null) return null

        return when (val classifier = targetType.classifier) {
            String::class -> value.toString()
            Int::class -> value.toString().toIntOrNull() ?: 0
            Long::class -> value.toString().toLongOrNull() ?: 0L
            Double::class -> value.toString().toDoubleOrNull() ?: 0.0
            Float::class -> value.toString().toFloatOrNull() ?: 0f
            Boolean::class -> value.toString().toBoolean()
            else -> value
        }
    }
}