package com.pine.pinedroid.db

import com.pine.pinedroid.db.bean.BaseDataTable
import com.pine.pinedroid.utils.camelToSnakeCase
import com.pine.pinedroid.utils.gson
import com.pine.pinedroid.utils.log.logd
import com.pine.pinedroid.utils.log.loge
import com.pine.pinedroid.utils.log.logw
import java.lang.reflect.InvocationTargetException
import java.util.Date
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType

inline fun <reified T : Any> model(db: String? = null): ModelK<T> = ModelK(T::class, db)

class ModelK<T : Any>(private var kclass: KClass<T>, dbName: String? = null) {

    private val constructor by lazy { kclass.constructors.first() }
    private val parameters by lazy {  constructor.parameters }

    private val model = Model(kclass.simpleName!!, dbName)

    /**
     * 根据主键或条件查找单条记录，并转换为指定类型
     */
    fun find(id: Number? = null): T? {
        val dbRecord = model.find(id?.toLong()) ?: return null
        return convertToType(dbRecord)
    }


    fun select(columns: String = "*"): List<T> {
        val dbRecords = model.select(columns).map {  convertToType(it)!! }
        return dbRecords
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

    // 添加 orWhere 相关方法
    fun whereOr(key: String, value: Any?): ModelK<T> {
        model.whereOr(key, value)
        return this
    }

    fun whereOr(raw: String): ModelK<T> {
        model.whereOr(raw)
        return this
    }

    fun whereOr(key: String, condition: String, value: Any?): ModelK<T> {
        model.whereOr(key, condition, value)
        return this
    }

    fun limit(count: Int, offset: Int? = null): ModelK<T> {
        model.limit(count, offset)
        return this
    }

    fun order(key: String): ModelK<T> {
        model.order(key)
        return this
    }

    fun count(): Long {
        return model.count()
    }

    fun drop(){
        return model.drop()
    }

    fun truncate(){
        return model.truncate()

    }


    /**
     * 将 DbRecord 转换为指定类型 T
     */
    private fun convertToType(dbRecord: DbRecord): T? {
        return try {
            val args = parameters.associate { param ->
                val paramName = param.name ?: ""
                val dbValue = findValueInRecord(dbRecord, paramName)
                val convertedValue = convertValue(dbValue, param.type)
                //logd("Db", "$paramName: $dbValue ${param.type} -> $convertedValue")
                param to convertedValue
            }
            //logd("convertToType: ${kclass.simpleName}", args)
            try {
                constructor.callBy(args)
            } catch (e: InvocationTargetException) {
                loge("DB", "序列化失败，A field should not be null, but null gave for ${kclass.simpleName}")
                parameters.forEach { param ->
                    val paramName = param.name ?: ""
                    val dbValue = findValueInRecord(dbRecord, paramName)
                    val convertedValue = convertValue(dbValue, param.type)
                    loge("Db", "$paramName: $dbValue ${param.type} -> $convertedValue")
                }
                null
            }

        } catch (e: Exception) {
            logw("DB", "(is Data class?)Error converting DbRecord to ${kclass.simpleName}: ${e.message}")
            e.printStackTrace()
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
        //logd(targetType.classifier)
        return when (val classifier = targetType.classifier) {
            String::class -> value.toString()
            Int::class -> value.toString().toIntOrNull() ?: 0
            Long::class -> value.toString().toLongOrNull() ?: 0L
            Double::class -> value.toString().toDoubleOrNull() ?: 0.0
            Float::class -> value.toString().toFloatOrNull() ?: 0f
            Boolean::class -> when (value) {
                "0", 0, 0L, false -> false
                "1", 1, 1L, true -> true
                is String -> value.toBooleanStrictOrNull() ?: false
                else -> value.toString().toBoolean()
            }
            Date::class -> Date(value.toString().toLongOrNull() ?: 0L)
            Map::class -> {
                try {
                    gson.fromJson<Map<*, *>>(value.toString(), targetType.javaType)
                } catch (e: Exception) {
                    loge("DatabaseConvert1", "value: $value")
                    loge("DatabaseConvert1", "Map conversion failed: ${e.message}")
                    emptyMap<String, Any>()
                }
            }
            List::class -> {
                try {
                    gson.fromJson<List<*>>(value.toString(), targetType.javaType)
                } catch (e: Exception) {
                    loge("DatabaseConvert2", "List conversion failed: ${e.message}")
                    emptyList<Any>()
                }
            }
            else -> {
                try {
                    gson.fromJson(value.toString(), targetType.javaType)
                } catch (e: Exception) {
                    loge("DatabaseConvert", "Object conversion failed: ${e.message}")
                    null
                }
            }
        }
    }

}