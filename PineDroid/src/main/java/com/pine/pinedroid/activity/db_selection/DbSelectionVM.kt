package com.pine.pinedroid.activity.db_selection

import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.db.AppDatabases
import com.pine.pinedroid.db.bean.DatabaseInfo

import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
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
            _viewState.update { currentState ->
                currentState.copy(
                    dbs = AppDatabases.getDatabaseFiles()
                )
            }

        }
    }

    fun onOpenDb(databaseInfo: DatabaseInfo) {
        navigateTo("table/${databaseInfo.name}")

    }


}