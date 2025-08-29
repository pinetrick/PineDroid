package com.pine.pinedroid.db

import com.pine.pinedroid.db.bean.TableInfo
import com.pine.pinedroid.utils.reflect.getCompanionObjVar
import java.util.Date
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.jvmErasure

inline fun <reified T : Any> table(): TableK<T> = TableK<T>(T::class)

class TableK<T : Any>(private var kclass: KClass<T>) {

    private val table by lazy {
        val dbName: String? = kclass.getCompanionObjVar("dbName")
        Table(kclass.simpleName!!, dbName)
    }


    fun createTable() {
        kclass.declaredMemberProperties.forEach { field ->
            // 跳过编译器生成的字段 (一般以 $ 开头)
            if (field.name.startsWith("$")) return@forEach
            if (field.name.startsWith("_")) return@forEach

            val name = field.name
            val type = when (field.returnType) {
                Int::class.java, java.lang.Integer::class.java -> "INTEGER"
                Long::class.java, java.lang.Long::class.java -> "BIGINT"
                String::class.java -> "TEXT"
                Boolean::class.java, java.lang.Boolean::class.java -> "INTEGER"
                Date::class.java -> "INTEGER"
                Double::class.java, java.lang.Double::class.java -> "REAL"
                Float::class.java, java.lang.Float::class.java -> "REAL"
                else -> "TEXT" //序列化存储
            }

            table.column(name, type)

        }
        table.createTable()
    }
}