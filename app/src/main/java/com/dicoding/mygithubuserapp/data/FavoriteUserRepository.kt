package com.dicoding.mygithubuserapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.dicoding.mygithubuserapp.data.local.entity.FavoriteUser
import com.dicoding.mygithubuserapp.data.local.room.FavoriteUserDao

class FavoriteUserRepository private constructor(
    private val favoriteUserDao: FavoriteUserDao,
) {
    fun getFavoriteUser(): LiveData<Result<List<FavoriteUser>>> = liveData {
        emit(Result.Loading)
        val localData: LiveData<Result<List<FavoriteUser>>> = favoriteUserDao.getFavoriteUser().map { Result.Success(it) }
        emitSource(localData)
    }

    suspend fun insert(user: FavoriteUser) {
        favoriteUserDao.insertUser(user)
    }

    suspend fun update(user: FavoriteUser) {
        favoriteUserDao.updateUser(user)
    }

    suspend fun delete(user: FavoriteUser) {
        favoriteUserDao.deleteUser(user)
    }

    fun getFavoriteUserByUsername(username: String): LiveData<FavoriteUser> {
        return favoriteUserDao.getFavoriteUserByUsername(username)
    }

    companion object {
        @Volatile
        private var instance: FavoriteUserRepository? = null
        fun getInstance(
            favoriteUserDao: FavoriteUserDao
        ): FavoriteUserRepository =
            instance ?: synchronized(this) {
                instance ?: FavoriteUserRepository(favoriteUserDao)
            }.also { instance = it }
    }
}