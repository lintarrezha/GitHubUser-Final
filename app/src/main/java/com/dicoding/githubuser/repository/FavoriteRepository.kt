package com.dicoding.githubuser.repository

import androidx.lifecycle.LiveData
import com.dicoding.githubuser.database.FavoriteDao
import com.dicoding.githubuser.database.FavoriteUser
import java.util.concurrent.ExecutorService

class FavoriteRepository private constructor(private val favoriteDao: FavoriteDao, private val executorService: ExecutorService){

    /*init {
        val database = FavoriteRoomDatabase.getDatabase(favoriteDao)
        favoriteDao = database.favoriteDao()
    }*/

    fun getFavoriteUsers(): LiveData<List<FavoriteUser>> = favoriteDao.getFavoriteUsers()

    fun getFavoriteUserByUsername(username: String): LiveData<FavoriteUser> = favoriteDao.getFavoriteUserByUsername(username)

    fun insert(favoriteUser: FavoriteUser) {
        executorService.execute { favoriteDao.insert(favoriteUser) }
    }

    fun delete(favoriteUser: FavoriteUser) {
        executorService.execute { favoriteDao.delete(favoriteUser) }
    }

    companion object {
        @Volatile
        private var INSTANCE: FavoriteRepository? = null

        fun getInstance(favoriteDao: FavoriteDao, executorService: ExecutorService): FavoriteRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FavoriteRepository(favoriteDao, executorService).also { INSTANCE = it }
            }
        }
    }

}