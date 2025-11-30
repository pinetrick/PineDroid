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

/**
 * 扩展函数：String.invokeFunction
 * 作用：根据字符串形式的类名+方法名，使用反射调用对应方法。
 *
 * 使用示例：
 * "com.example.Callbacks.onUserLoaded".invokeFunction(3, "Jack")
 *
 * 注意：
 * 1. callbackFunction 格式必须是 "包名.类名.方法名"
 * 2. 支持 Kotlin object 的 静态方法
 * 3. 参数类型必须与方法签名一致
 */
fun String.invokeStaticFunction(vararg args: Any?): Any? {
    if (this.isEmpty()) return null

    try {
        // 拆分类名和方法名
        val lastDot = this.lastIndexOf('.')
        require(lastDot > 0) { "Invalid callbackFunction: $this" }

        val className = this.substring(0, lastDot)
        val methodName = this.substring(lastDot + 1)

        // 获取 Class
        val clazz = Class.forName(className)

        // 处理 Kotlin object 类
        val instance = if (clazz.declaredFields.any { it.name == "INSTANCE" } &&
            clazz.enclosingClass == null) {
            // 对于 Kotlin object，需要获取 INSTANCE 字段
            clazz.getDeclaredField("INSTANCE").get(null)
        } else {
            null // 对于普通类，静态方法不需要实例
        }

        // 查找方法（第一个匹配方法名的 public 方法）
        val method = clazz.methods.firstOrNull { it.name == methodName }
            ?: throw NoSuchMethodException("Method $methodName not found in $className")

        // 转成数组调用
        return method.invoke(instance, *args)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null

}