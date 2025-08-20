package com.pine.pinedroid.utils

import android.widget.Toast

fun toast(message: String){
    Toast.makeText(activityContext, message, Toast.LENGTH_SHORT).show()

}