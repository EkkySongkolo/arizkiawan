package com.rizki.submisionandroidfudamental.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rizki.submisionandroidfudamental.data.model.Item

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: Item)

    @Query("SELECT * FROM User")
    fun loadAll(): LiveData<MutableList<Item>>

    @Query("SELECT * FROM User WHERE id LIKE :id LIMIT 1")
    fun findById(id: Int): Item

    @Delete
    fun delete(user: Item)
}