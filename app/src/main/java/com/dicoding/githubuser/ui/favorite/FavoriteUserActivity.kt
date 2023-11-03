package com.dicoding.githubuser.ui.favorite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.githubuser.database.FavoriteUser
import com.dicoding.githubuser.databinding.ActivityFavoriteUserBinding
import com.dicoding.githubuser.detail.DetailUserActivity
import com.dicoding.githubuser.helper.ViewModelFactory
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.util.query

class FavoriteUserActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFavoriteUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Favorite User"


        //mengatur layout manager untuk RecyclerView
        val layoutManager = LinearLayoutManager(this)
        binding.rvFavUser.layoutManager = layoutManager

        //membuat adapter untuk RecyclerView
        val adapter = FavoriteUserAdapter()
        adapter.setOnItemClickCallback(object : FavoriteUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: FavoriteUser) {
                //ketika item dalam RecyclerView diklik, buat Intent untuk membuka DetailUserActivity
                Intent(this@FavoriteUserActivity, DetailUserActivity::class.java)
                    .also {
                        //mengirim data username dan avatarUrl ke DetailUserActivity
                        it.putExtra(DetailUserActivity.EXTRA_ID, data.username)
                        it.putExtra(DetailUserActivity.EXTRA_URL, data.avatarUrl)
                        startActivity(it)
                    }
            }
        })

        val favoriteUserViewModel by viewModels<FavoriteUserViewModel> {
            ViewModelFactory.getInstance(this.applicationContext)
        }
        //mengamati daftar favorite users dari viewmodel
        favoriteUserViewModel.getFavoriteUsers().observe(this) { users ->
            val user = arrayListOf<FavoriteUser>()
            //mengkonversi setiap item dalam daftar favorite users menjadi FavoriteUser yang digunakan dalam adapter
            users.map {
                val item = FavoriteUser(username = it.username, avatarUrl = it.avatarUrl)
                user.add(item)
            }
            //mengirim data ke adapter
            adapter.submitList(user)
        }

        binding.rvFavUser.adapter = adapter
    }
}

