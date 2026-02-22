package com.pine.pinedroid.activity.sql


import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.db.DbRecord
import com.pine.pinedroid.db.model
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RunSqlScreenVM : BaseViewModel<RunSqlScreenStatus>(RunSqlScreenStatus::class) {

    fun initialize(dbName: String, tableName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.update { it.copy(dbName = dbName, tableName = tableName) }
            _viewState.update { it.copy(sql = getDefaultSql()) }
            runQueryOnIo()
        }
    }

    fun getDefaultSql(): String {
        val vs = _viewState.value
        return "SELECT *\nFROM " + vs.tableName + "\nLIMIT 100"
    }

    fun updateSql(sql: String) {
        _viewState.update { it.copy(sql = sql) }
    }

    fun onRunSql() {
        viewModelScope.launch(Dispatchers.IO) {
            runQueryOnIo()
        }
    }

    private suspend fun runQueryOnIo() {
        _viewState.update { it.copy(isLoading = true, error = null) }
        try {
            val vs = _viewState.value
            val queryResult = model(vs.tableName, vs.dbName).rawQuery(vs.sql)
            _viewState.update {
                it.copy(
                    table = queryResult.first,
                    tableHeader = queryResult.second,
                    isLoading = false,
                )
            }
        } catch (e: Exception) {
            _viewState.update { it.copy(isLoading = false, error = e.message) }
        }
    }

    // ── 编辑 ──────────────────────────────────────────────

    fun startEdit(record: DbRecord, columnName: String, currentValue: String) {
        _viewState.update { it.copy(editingCell = EditingCell(record, columnName, currentValue)) }
    }

    fun cancelEdit() {
        _viewState.update { it.copy(editingCell = null) }
    }

    fun confirmEdit(newValue: String) {
        val editing = _viewState.value.editingCell ?: return
        viewModelScope.launch(Dispatchers.IO) {
            editing.record[editing.columnName] = newValue
            editing.record.save()
            _viewState.update { it.copy(editingCell = null) }
            runQueryOnIo()
        }
    }
}
