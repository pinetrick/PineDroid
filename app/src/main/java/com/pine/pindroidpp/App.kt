package com.pine.pindroidpp

import android.app.Application
import com.pine.pinedroid.PineConfig

class App: Application()
{
    override fun onCreate() {
        super.onCreate()
        PineConfig.setIsDebugWindowAlwaysOn(true)
    }
}