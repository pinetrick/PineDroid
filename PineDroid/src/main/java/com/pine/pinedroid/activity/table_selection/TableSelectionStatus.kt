package com.pine.pinedroid.activity.table_selection


import com.pine.pinedroid.db.bean.DatabaseInfo
import com.pine.pinedroid.db.bean.TableInfo


data class TableSelectionStatus(
    val dbName: String = "Default Database",
    val tables: List<TableInfo> = listOf<TableInfo>()
){


}

