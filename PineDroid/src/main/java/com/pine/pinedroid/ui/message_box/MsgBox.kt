package com.pine.pinedroid.ui.message_box

import android.content.Context
import com.pine.pinedroid.utils.activityContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class MsgBox : MessageBox(activityContext) {

    private var continuation: ((Int) -> Unit)? = null

    inner class MsgBoxListener : OnMessageClickListener {
        override fun messageBoxChoose(id: Int) {

            continuation?.invoke(id)
            continuation = null
        }
    }

    suspend operator fun invoke(message: String, btn1: String, btn2: String, btn3: String, btn4: String): Int {
        return suspendCancellableCoroutine { cont ->
            continuation = cont::resume
            setListener(MsgBoxListener())
            super.show(message, btn1, btn2, btn3, btn4)
        }
    }

    suspend operator fun invoke(message: String, btn1: String, btn2: String, btn3: String): Int {
        return suspendCancellableCoroutine { cont ->
            continuation = cont::resume
            setListener(MsgBoxListener())
            super.show(message, btn1, btn2, btn3)
        }
    }

    suspend operator fun invoke(message: String, btn1: String, btn2: String): Int {
        return suspendCancellableCoroutine { cont ->
            continuation = cont::resume
            setListener(MsgBoxListener())
            super.show(message, btn1, btn2)
        }
    }

    suspend operator fun invoke(message: String, btn: String): Int {
        return suspendCancellableCoroutine { cont ->
            continuation = cont::resume
            setListener(MsgBoxListener())
            super.show(message, btn)
        }
    }

    suspend operator fun invoke(message: String): Int {
        return suspendCancellableCoroutine { cont ->
            continuation = cont::resume
            setListener(MsgBoxListener())
            super.show(message)
        }
    }


}