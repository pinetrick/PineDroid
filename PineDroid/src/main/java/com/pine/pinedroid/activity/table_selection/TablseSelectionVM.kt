package com.pine.pinedroid.activity.table_selection

import com.pine.pinedroid.activity.db_selection.DbSelectionStatus
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TablseSelectionVM : BaseViewModel() {
    private val _viewState = MutableStateFlow(DbSelectionStatus())
    val viewState: StateFlow<DbSelectionStatus> = _viewState

    fun onReturn() {

    }


}