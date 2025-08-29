package com.pine.pindroidpp.db.bean

import com.pine.pinedroid.db.bean.BaseDataTable
import com.pine.pinedroid.db.model
import java.util.Date

data class User(
    override var id: Long? = null,

    var username: String,
    var password: String,
    var user_profile_id: Int? = null,
    var create_time: Date = Date()
) : BaseDataTable() {
    fun articles() = model<Article>().where("user_id", id).select()
    fun profile() = model<UserProfile>().find(user_profile_id)
    fun house() = model<House>().where("user_id", id).find()


}