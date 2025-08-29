package com.pine.pindroidpp.db

import com.pine.pindroidpp.db.bean.Article
import com.pine.pindroidpp.db.bean.House
import com.pine.pinedroid.db.table
import com.pine.pindroidpp.db.bean.User
import com.pine.pindroidpp.db.bean.UserProfile
import com.pine.pinedroid.db.model

object TableTest {

    fun createTables() {
        table<Article>().createTable()
        table<House>().createTable()
        table<User>().createTable()
        table<UserProfile>().createTable()
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
        val user = model<User>().where("username", "York").find()!!
        val articles: List<Article> = user.articles()
        for (article in articles) {
            println("Article: ${article.title}")
        }
    }
}