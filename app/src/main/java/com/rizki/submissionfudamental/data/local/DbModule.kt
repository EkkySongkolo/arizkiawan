package com.rizki.submisionandroidfudamental.data.local

import android.content.Context
import androidx.room.Room

class DbModule(context: Context) {
    private val db = Room.databaseBuilder(context, AppDb::class.java, "usergithub.db")
        .allowMainThreadQueries()
        .build()

    val userDao = db.userDao()
}