package com.dicoding.mygithubuserapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dicoding.mygithubuserapp.data.local.entity.FavoriteUser

@Dao
interface FavoriteUserDao {
    @Query("SELECT * FROM FavoriteUser")
    fun getFavoriteUser(): LiveData<List<FavoriteUser>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: FavoriteUser)

    @Update
    suspend fun updateUser(user: FavoriteUser)

    @Delete
    suspend fun deleteUser(user: FavoriteUser)

    @Query("SELECT * FROM FavoriteUser WHERE username = :username")
    fun getFavoriteUserByUsername(username: String): LiveData<FavoriteUser>
}