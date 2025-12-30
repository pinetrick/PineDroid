package com.pine.pinedroid.hardware.step_counter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import com.pine.pinedroid.utils.log.loge
import com.pine.pinedroid.utils.shrinker_keep.Keep
import com.pine.pinedroid.utils.toast

@Keep
class ActivityRecognitionReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {

    }
}
