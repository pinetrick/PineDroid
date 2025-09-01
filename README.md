# PineDroid

## Overview

PineDroid is an Android library/module designed to provide helpful debugging tools and utilities for Android applications.

This module includes features such as:
* A floating debug window for runtime inspection
* Content provider for sharing specific app data
* Utilities for application context and activity lifecycle management


## Features

***Database ORM***
```kotlin
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
```
*   **Debug Tools:**


### Installation / Integration
on your `build.gradle.kts`
implementation("com.github.pinetrick:PineDroid:0.0.18")

