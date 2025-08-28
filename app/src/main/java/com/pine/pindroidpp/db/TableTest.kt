package com.pine.pindroidpp.db

import com.pine.pindroidpp.db.bean.Article
import com.pine.pinedroid.db.table
import com.pine.pindroidpp.db.bean.User
import com.pine.pinedroid.db.model

object TableTest {

    fun createTables() {
        table<User>().createTable()
    }

    fun testInsert(){
        User(
            username = "Pine",
            password = "password1",
        ).save()
        User(
            username = "York",
            password = "password2",
        ).save()
    }

    fun testSearch() {
        val user = model<User>().where("username", "Pine").find()
        user?.delete()

        val users =  model<User>().where("username", "Pine").select()
        users.forEach { user ->
            user.password = "newPwd"
            user.save()
        }
    }

    fun loadRelative(){
        val user = model<User>().where("username", "York").find()
        val articles = user?.relation<Article>("articles")?.select()
    }
}