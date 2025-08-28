package com.pine.pindroidpp.db.bean

import com.pine.pinedroid.db.DbConnection
import com.pine.pinedroid.db.bean.BaseDataTable
import com.pine.pinedroid.db.bean.BelongsTo
import com.pine.pinedroid.db.bean.HasMany
import com.pine.pinedroid.db.bean.HasOne
import com.pine.pinedroid.db.bean.Relation

import java.util.Date

data class User (
    //Not Allowed start with '_'
    override var id: Long? = null,
    override var relations: Map<String, Relation<out BaseDataTable>> = mapOf(
        "articles" to HasMany<Article>("user_id"),
        "user_profile" to BelongsTo<UserProfile>("user_profile_id"),
        "house" to HasOne<House>("user_id"),
    ),

    var username: String,
    var password: String,
    var user_profile_id: Int? = null,
    var create_time: Date = Date()
) : BaseDataTable() {
//    fun articles() = HasMany(this, Article::class,"user_id")
//    fun profile() = BelongsTo(this, UserProfile::class,"user_profile_id")
//    fun house() = HasOne(this, House::class, "user_id")


}