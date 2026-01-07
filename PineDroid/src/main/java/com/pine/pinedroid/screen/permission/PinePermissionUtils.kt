package com.pine.pinedroid.screen.permission

import android.os.Build
import com.pine.pinedroid.screen.permission.location.PinePermissionActivity
import com.pine.pinedroid.utils.currentActivity
import com.pine.pinedroid.utils.pineActivityStart
import kotlinx.coroutines.CompletableDeferred

object PinePermissionUtils {

    private var permissionDeferred: CompletableDeferred<Boolean?>? = null

    fun hasPermission(permission: PineOnePermission): Boolean {
        return permission.hasPermission()
    }


    suspend fun requestPermissions(
        permissions: List<PineOnePermission>,
        onPermission: suspend (Boolean) -> Unit
    ) {
        var allGranted = true

        permissions.forEach { permission ->
            if (Build.VERSION.SDK_INT in permission.requireApiLevel) {
                if (allGranted) {
                    requestPermission(permission) { isPermissionGranted ->
                        if ((!permission.optional) && (!isPermissionGranted)) {
                            allGranted = false
                            return@requestPermission
                        }
                    }
                }
            }
        }
        onPermission(allGranted)
    }


    suspend fun requestPermission(
        permission: PineOnePermission,
        onPermission: suspend (isPermissionGranted: Boolean) -> Unit
    ) {
        if (hasPermission(permission)) return onPermission(true)

        // 创建新的Deferred对象
        permissionDeferred = CompletableDeferred()

        PinePermissionActivity.state = permission
        pineActivityStart<PinePermissionActivity>()

        // 等待权限请求结果
        val result = permissionDeferred?.await()

        // 处理权限结果
        val granted = result ?: false
        onPermission(granted)

        // 清理
        permissionDeferred = null
    }

    /**
     * 权限请求结果回调
     * true - 已授权
     * false - 拒绝
     * null - 永久拒绝
     */
    fun onPermission(enabled: Boolean?) {
        currentActivity.finish()
        // 完成Deferred对象
        permissionDeferred?.complete(enabled)
    }


}