package com.pine.pinedroid.activity.table_selection

import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.activity.db_selection.DbSelectionStatus
import com.pine.pinedroid.db.AppDatabases
import com.pine.pinedroid.db.bean.DatabaseInfo
import com.pine.pinedroid.db.bean.TableInfo
import com.pine.pinedroid.db.db
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TableSelectionVM : BaseViewModel<TableSelectionStatus>(TableSelectionStatus::class) {

    fun initialize(dbName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.update { it.copy(dbName = dbName, isLoading = true) }
            val tables = db(dbName).tables()
            _viewState.update { currentState ->
                currentState.copy(
                    tables = tables,
                    isLoading = false,
                )
            }
            if (tables.size == 1) {
                onOpenTable(tables.first())
            }
        }
    }

    fun onOpenTable(tableInfo: TableInfo) {
        val dbName = _viewState.value.dbName

        navigateTo("sql/${dbName}/${tableInfo.name}")

    }



}