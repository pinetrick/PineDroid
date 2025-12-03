package com.pine.pinedroid.utils.shrinker_keep

//标记不应该被压缩的类
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Keep
