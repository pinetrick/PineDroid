package com.pine.pinedroid.activity.sql



import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.activity.db_selection.DbSelectionStatus
import com.pine.pinedroid.db.AppDatabases
import com.pine.pinedroid.db.bean.DatabaseInfo
import com.pine.pinedroid.db.bean.TableInfo
import com.pine.pinedroid.db.db
import com.pine.pinedroid.db.model
import com.pine.pinedroid.db.table
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RunSqlScreenVM : BaseViewModel() {
    private val _viewState = MutableStateFlow(RunSqlScreenStatus())
    val viewState: StateFlow<RunSqlScreenStatus> = _viewState

    fun initialize(dbName: String, tableName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.update { currentState ->
                currentState.copy(
                    dbName = dbName,
                    tableName = tableName,
                )
            }
            _viewState.update { currentState ->
                currentState.copy(
                    sql = getDefaultSql()
                )
            }
            onRunSql()
        }
    }

    fun getDefaultSql(): String {
        val vs = _viewState.value
        return "SELECT *\n" +
                "FROM " + vs.tableName + "\n" +
                "LIMIT 100"
    }

    fun updateSql(sql: String) {
        _viewState.update { currentState ->
            currentState.copy(
                sql = sql,
            )
        }
    }

    fun onRunSql() {
        val vs = _viewState.value
        val queryResult = model(vs.tableName, vs.dbName).rawQuery(vs.sql)
        _viewState.update { currentState ->
            currentState.copy(
                table = queryResult.first,
                tableHeader = queryResult.second,
            )
        }
    }


}