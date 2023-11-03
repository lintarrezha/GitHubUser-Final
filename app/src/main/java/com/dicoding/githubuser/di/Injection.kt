package com.dicoding.githubuser.di

import android.content.Context
import com.dicoding.githubuser.database.FavoriteRoomDatabase
import com.dicoding.githubuser.repository.FavoriteRepository
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object Injection {
    fun provideRepository(context: Context): FavoriteRepository {
        val database = FavoriteRoomDatabase.getDatabase(context)
        val dao = database.favoriteDao()
        val executorService = Executors.newSingleThreadExecutor()
        return FavoriteRepository.getInstance(dao, executorService)
    }
}