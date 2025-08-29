package com.pine.pinedroid.activity.db_selection

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.db.AppDatabases
import com.pine.pinedroid.db.bean.DatabaseInfo

import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import com.pine.pinedroid.utils.activityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class DbSelectionVM : BaseViewModel() {
    private val _viewState = MutableStateFlow(DbSelectionStatus())
    val viewState: StateFlow<DbSelectionStatus> = _viewState

    fun initialize() {
        viewModelScope.launch(Dispatchers.IO) {
            val dbs = AppDatabases.getDatabaseFiles()
            _viewState.update { currentState ->
                currentState.copy(
                    dbs = dbs
                )
            }

        }
    }

    fun onNew(){

    }

    fun onOpenDb(databaseInfo: DatabaseInfo) {
        navigateTo("table/${databaseInfo.name}")

    }

    fun onClose() {
        (activityContext as Activity).finish()

    }


}