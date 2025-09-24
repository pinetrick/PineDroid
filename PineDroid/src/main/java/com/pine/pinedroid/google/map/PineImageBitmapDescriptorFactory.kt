package com.pine.pinedroid.google.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.pine.pinedroid.net.findLocalFile
import com.pine.pinedroid.utils.appContext

class PineImageBitmapDescriptorFactory {
    private val reusableTextView: TextView by lazy {
        TextView(appContext).also {
            // 必须先设置 LayoutParams
            it.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private val reusableBitmap: Bitmap by lazy {
        Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888)
    }

    fun createTextBitmapDescriptor(url: String?, text: String?): BitmapDescriptor {
        val file = url.findLocalFile()
        reusableTextView.apply {
            this.text = text
            setTextColor(Color.BLACK)
            setBackgroundColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        // 复用 Bitmap
        reusableBitmap.eraseColor(Color.TRANSPARENT)
        val canvas = Canvas(reusableBitmap)
        reusableTextView.measure(
            View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.AT_MOST)
        )
        reusableTextView.layout(0, 0, 200, 100)
        reusableTextView.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(reusableBitmap)
    }
}