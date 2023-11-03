package com.dicoding.githubuser.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.githubuser.database.FavoriteUser
import com.dicoding.githubuser.repository.FavoriteRepository

class FavoriteUserViewModel(private val favoriteRepository: FavoriteRepository): ViewModel() {

    fun getFavoriteUserByUsername(username: String): LiveData<FavoriteUser> = favoriteRepository.getFavoriteUserByUsername(username)

    fun getFavoriteUsers(): LiveData<List<FavoriteUser>> = favoriteRepository.getFavoriteUsers()

    fun insert(favoriteUser: FavoriteUser){
        favoriteRepository.insert(favoriteUser)
    }

    fun delete(favoriteUser: FavoriteUser){
        favoriteRepository.delete(favoriteUser)
    }

}