############################################

## 1️⃣ Bean 类及 BaseDataTable 系列

############################################


# 保留 BaseDataTable 继承体系
-keep class * extends com.pine.pinedroid.db.bean.BaseDataTable { *; }


############################################

## 2️⃣ Keep 注解标记的类与成员

############################################

# 保留所有使用 @Keep 注解的类及其成员

-keep @com.pine.pinedroid.utils.shrinker_keep.Keep class * { *; }
-keepclassmembers class * {
@com.pine.pinedroid.utils.shrinker_keep.Keep *;
}



-keep class * extends androidx.lifecycle.ViewModel { <init>(...);}


############################################

## 4️⃣ Compose 与 Kotlin 核心库

############################################
-keep class androidx.compose.** { *; }
-keep class kotlin.** { *; }
-dontwarn androidx.compose.**


############################################

## 6️⃣ Kotlin Serialization 序列化相关

############################################

# 保留所有 @Serializable 类及其序列化器

-keep @kotlinx.serialization.Serializable class *{ *; }
-keep class *$$serializer { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable  *;
}

# 可选：保留 @SerialName 注解字段

-keepclassmembers class * {
    @kotlinx.serialization.SerialName *;
}

############################################

## 7️⃣ 保留泛型签名、内部类、注解等元信息

############################################
-keepattributes Signature,InnerClasses,EnclosingMethod,Annotation,RuntimeVisibleAnnotations,AnnotationDefault


#
## Firebase Auth
#-keep class com.google.firebase.auth.** { *; }
#-keep class com.google.firebase.auth.internal.** { *; }
#-keep class com.google.firebase.auth.api.** { *; }
#-keep class com.google.firebase.auth.ktx.** { *; }
#-keepclassmembers class com.google.firebase.auth.** { *; }
## Google Sign-In
#-keep class com.google.android.gms.auth.api.signin.** { *; }
#-keepclassmembers class com.google.android.gms.auth.api.signin.** { *; }
## Kotlin 协程
#-keepclassmembers class kotlin.coroutines.Continuation { *; }
#-keepclassmembers class kotlinx.coroutines.** { *; }
## Gson
#-keep class com.google.firebase.** { *; }
#-keepattributes Signature
#-keepattributes *Annotation*