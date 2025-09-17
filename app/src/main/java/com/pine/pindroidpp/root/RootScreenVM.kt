package com.pine.pindroidpp.root

import androidx.lifecycle.viewModelScope
import com.pine.pindroidpp.db.TableTest
import com.pine.pinedroid.activity.image_pickup.ImagePickup
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import com.pine.pinedroid.ui.message_box.MessageBox
import com.pine.pinedroid.utils.toast
import kotlinx.coroutines.launch


class RootScreenVM : BaseViewModel<RootScreenState>(RootScreenState::class) {


    fun onInit() {

    }

    fun onTakePhoto() = viewModelScope.launch {
        ImagePickup.takePhoto(
            allowVideo = true,
            useSystemCamera = true,
        ) { oneImage ->
            toast(oneImage.toString())
        }
    }

    fun onImagePickUp() = viewModelScope.launch {
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