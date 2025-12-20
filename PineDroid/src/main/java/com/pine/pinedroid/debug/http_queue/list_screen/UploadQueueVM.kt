package com.pine.pinedroid.debug.http_queue.list_screen

import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.db.model
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import com.pine.pinedroid.net.http_queue.PineHttpQueue
import com.pine.pinedroid.net.http_queue.bean.PendingPostRequest
import com.pine.pinedroid.ui.message_box.MsgBox
import com.pine.pinedroid.utils.log.logd
import com.pine.pinedroid.utils.toast
import kotlinx.coroutines.launch

// 更新 ViewModel 添加删除和上传方法
class UploadQueueVM : BaseViewModel<UploadQueueState>(UploadQueueState::class) {

    fun onInit() {
        val lists = model<PendingPostRequest>().select()
        setStateSync {
            copy(
                queues = lists,
                isLoading = false
            )
        }
        logd(lists)
    }

    fun processQueue(){
        PineHttpQueue.i.tryStartProcessing()
        toast("队列已运行")
    }

    fun deleteRequest(request: PendingPostRequest) = viewModelScope.launch {
        val confirm = MsgBox().invoke("Do you want delete this task?", "No", "Yes")
        if (confirm == 1)  return@launch

        request.delete()
        onInit()
    }

    fun uploadRequest(request: PendingPostRequest) = viewModelScope.launch {
        PineHttpQueue.i.handlePendingRequest(request)
        onInit()
    }
}