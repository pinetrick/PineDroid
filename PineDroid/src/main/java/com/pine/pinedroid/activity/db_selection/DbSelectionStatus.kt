package com.pine.pinedroid.activity.db_selection

import com.pine.pinedroid.db.bean.DatabaseInfo
import com.pine.pinedroid.utils.shrinker_keep.Keep

@Keep
data class DbSelectionStatus(
    val dbs: List<DatabaseInfo> = listOf<DatabaseInfo>()
){


}

