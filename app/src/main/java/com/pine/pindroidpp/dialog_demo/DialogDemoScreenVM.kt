package com.pine.pindroidpp.dialog_demo

import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import com.pine.pinedroid.ui.bottom_input.PineBottomEditText
import com.pine.pinedroid.ui.message_box.MessageBox
import com.pine.pinedroid.ui.message_box.MsgBox
import kotlinx.coroutines.launch

class DialogDemoScreenVM : BaseViewModel<DialogDemoScreenState>(DialogDemoScreenState::class) {

    fun showMsgBox1() = viewModelScope.launch {
        val result = MsgBox()("Single button message dialog", "OK")
        setStateSync { copy(lastResult = "MsgBox (1 btn): clicked button $result") }
    }

    fun showMsgBox2() = viewModelScope.launch {
        val result = MsgBox()("Choose an option to proceed", "Confirm", "Cancel")
        setStateSync { copy(lastResult = "MsgBox (2 btn): clicked button $result") }
    }

    fun showMsgBox3() = viewModelScope.launch {
        val result = MsgBox()("Which option do you prefer?", "Option A", "Option B", "Option C")
        setStateSync { copy(lastResult = "MsgBox (3 btn): clicked button $result") }
    }

    fun showMsgBox4() = viewModelScope.launch {
        val result = MsgBox()("Rate your experience:", "Excellent", "Good", "Fair", "Poor")
        setStateSync { copy(lastResult = "MsgBox (4 btn): clicked button $result") }
    }

    fun showMsgBoxClassic() = viewModelScope.launch {
        MessageBox.i().setListener { id ->
            setStateSync { copy(lastResult = "MessageBox (classic API): clicked $id") }
        }.show("Classic MessageBox", "Yes", "No")
    }

    fun showBottomInput() = viewModelScope.launch {
        val result = PineBottomEditText.show(
            placeholder = "Type your message here...",
            textButton = "Submit",
        )
        setStateSync {
            copy(lastResult = "BottomEditText: '${result ?: "(cancelled)"}'")
        }
    }
}
