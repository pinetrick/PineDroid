package com.pine.pinedroid.utils.reflect

import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.declaredMemberProperties

/**
 * 类反射获取T的伴随对象 var值
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any> KClass<*>.getCompanionObjVar(varName: String): T? {
    val companion = this.companionObjectInstance ?: return null
    val property = this.companionObject
        ?.declaredMemberProperties
        ?.firstOrNull { it.name == varName }
        ?: return null

    return property.getter.call(companion) as? T
}


fun<T : Any> createInstance(clazz: KClass<T>): T {
    return try {
        clazz.java.getDeclaredConstructor().newInstance()
    } catch (e: Exception) {
        throw IllegalArgumentException("Type ${clazz.simpleName} must have a no-args constructor")
    }
}
