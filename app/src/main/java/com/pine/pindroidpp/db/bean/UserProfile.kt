package com.pine.pindroidpp.db.bean

import com.pine.pinedroid.db.bean.BaseDataTable

data class UserProfile(
//    var id: Int, //will auto create Id if not exist
    var email: String,
): BaseDataTable()