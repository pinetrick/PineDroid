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

    private val table: Table
        get() {
            val dbName: String? = kclass.getCompanionObjVar("dbName")
            return Table(kclass.simpleName!!, dbName)
        }


    fun createTable(){
        createThisTable()
        createRelativeTable()

    }

    private fun createRelativeTable() {
        // 找到所有返回 BaseDataTable.Relation 的方法
//        val relations = kclass.declaredMemberFunctions.filter { fn ->
//            fn.returnType.jvmErasure.simpleName?.contains("Relation") == true
//        }
//
//        relations.forEach { fn ->
//            println("方法名: ${fn.name}")
//            println("参数个数: ${fn.parameters.size}") // 一般只有 this
//            println("返回类型: ${fn.returnType}")
//
//            // 如果你想真的调用，实例化一个对象后 call
//            val instance = kclass.constructors.first().call(
//                1, "name", "pwd", 123, Date()
//            )
//            val result = fn.call(instance)
//            println("执行结果: $result")
//            println("------------")
//        }
    }

    private fun createThisTable() {
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