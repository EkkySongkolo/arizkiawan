package com.rizki.submisionandroidfudamental.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rizki.submisionandroidfudamental.data.model.Item

@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun userDao(): UserDao
}