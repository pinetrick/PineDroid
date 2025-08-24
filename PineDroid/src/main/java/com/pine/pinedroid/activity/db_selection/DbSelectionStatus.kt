package com.pine.pinedroid.activity.db_selection

import com.pine.pinedroid.db.bean.DatabaseInfo


data class DbSelectionStatus(
    val dbs: List<DatabaseInfo> = listOf<DatabaseInfo>()
){


}

