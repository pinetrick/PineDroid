package com.pine.pinedroid.google

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.google.android.play.core.review.ReviewManagerFactory
import com.pine.pinedroid.utils.activityContext
import com.pine.pinedroid.utils.appContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object GooglePlay {


    fun requestGooglePlayReview() {
        val manager = ReviewManagerFactory.create(activityContext)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 拿到 ReviewInfo 对象
                val reviewInfo = task.result
                CoroutineScope(Dispatchers.Main).launch {
                    manager.launchReviewFlow(activityContext as Activity, reviewInfo)
                }
            } else {
                // 失败时，可以 fallback 到跳转 Play 商店页面
                openGooglePlay()
            }
        }
    }

    fun openGooglePlay() {
        val packageName = appContext.packageName
        try {
            activityContext.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        } catch (e: Exception) {
            activityContext.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    }

}