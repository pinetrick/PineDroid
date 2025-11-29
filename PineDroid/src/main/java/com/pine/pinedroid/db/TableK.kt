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
            val classifier = field.returnType.classifier

            val type = when (classifier) {
                Int::class     -> "INTEGER"
                Long::class    -> "BIGINT"
                String::class  -> "TEXT"
                Boolean::class -> "INTEGER"
                Double::class  -> "REAL"
                Float::class   -> "REAL"
                Date::class    -> "INTEGER"
                else -> "TEXT"
            }

            table.column(name, type)

        }
        table.createTable()
    }
}