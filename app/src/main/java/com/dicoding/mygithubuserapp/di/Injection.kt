package com.dicoding.mygithubuserapp.di

import android.content.Context
import com.dicoding.mygithubuserapp.data.FavoriteUserRepository
import com.dicoding.mygithubuserapp.data.local.room.FavoriteUserDatabase

object Injection {
    fun provideRepository(context: Context): FavoriteUserRepository {
        val database = FavoriteUserDatabase.getInstance(context)
        val dao = database.favoriteUserDao()
        return FavoriteUserRepository.getInstance(dao)
    }
}