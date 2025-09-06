package com.pine.pindroidpp.root

import com.pine.pindroidpp.db.TableTest
import com.pine.pinedroid.activity.image_pickup.ImagePickup
import com.pine.pinedroid.activity.image_pickup.preview.ImagePreviewScreenVM
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import com.pine.pinedroid.ui.message_box.MessageBox
import com.pine.pinedroid.utils.toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.toString


class RootScreenVM : BaseViewModel() {
    private val _viewState = MutableStateFlow(RootScreenState())
    val viewState: StateFlow<RootScreenState> = _viewState

    fun onInit() {

    }

    fun onTakePhoto() {
        ImagePickup.takePhoto(
            allowVideo = true,
            useSystemCamera = true,
        ) { oneImage ->
            toast(oneImage.toString())
        }
    }

    fun onImagePickUp() {
        ImagePickup.pickImageFromGallery(
            allowCamera = true,
            allowMultiple = true,
            allowVideo = true,
            useSystemCamera = true,
        ) { oneImage ->
            toast(oneImage.toString())
        }
    }

    fun onDbTest(){
        TableTest.createTables()
        TableTest.testInsert()
        TableTest.testSearch()
        TableTest.loadRelative()
        toast("Done")
    }

    fun onMessageBoxTest(){
        MessageBox.i().setListener { messageBoxChoose ->
            toast("Clicked: $messageBoxChoose")
        }.show("title" ,"btn1", "btn2")
    }
}