package com.dicoding.githubuser.detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.response.DetailUserResponse
import com.dicoding.githubuser.database.FavoriteUser
import com.dicoding.githubuser.databinding.ActivityDetailUserBinding
import com.dicoding.githubuser.helper.ViewModelFactory
import com.dicoding.githubuser.ui.favorite.FavoriteUserViewModel
import com.dicoding.githubuser.viewmodel.DetailViewModel
import com.google.android.material.tabs.TabLayoutMediator

class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private var favoriteUser = FavoriteUser()

    // Inisialisasi DetailViewModel menggunakan viewModels
    private val detailViewModel by viewModels<DetailViewModel>()

    // Inisialisasi FavoriteUserViewModel dengan ViewModelFactory
    private val favoriteUserViewModel by viewModels<FavoriteUserViewModel> {
        ViewModelFactory.getInstance(this.applicationContext)
    }

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_URL = "extra_url"

        @StringRes
        private val TAB_TITLE = intArrayOf(
            R.id.tvFollowing,
            R.id.tvFollowers
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_user)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = intent.getStringExtra(EXTRA_ID)
        detailViewModel.setDetailUser(detailUserId = items.toString())
        supportActionBar?.hide()

        // Memantau perubahan data detailUser dalam ViewModel
        detailViewModel.detailUser.observe(this) { detailUser ->
            setDetailUser(detailUser)

            favoriteUser.let {
                favoriteUser.username = detailUser.login
                favoriteUser.avatarUrl = detailUser.avatarUrl
            }
        }

        // Memantau apakah pengguna terkait adalah favorit atau tidak
        favoriteUserViewModel.getFavoriteUserByUsername(items.toString()).observe(this) { it
            if (it != null) {
                binding.fabFavorite.setImageResource(R.drawable.ic_favorite_fill)
                binding.fabFavorite.setOnClickListener {
                    favoriteUserViewModel.delete(favoriteUser)
                }
            } else {
                binding.fabFavorite.setImageResource(R.drawable.ic_favorite_outline)
                binding.fabFavorite.setOnClickListener {
                    favoriteUserViewModel.insert(favoriteUser)
                }
            }
        }

        // Inisialisasi adapter untuk ViewPager
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager2)
        viewPager.adapter = sectionsPagerAdapter

        // Menghubungkan TabLayout dan ViewPager
        val tabLayoutMediator = TabLayoutMediator(binding.tabs, binding.viewPager2) { tab, position ->
            tab.text = resources.getString(TAB_TITLE[position])
            when (position) {
                0 -> {
                    tab.text = getString(R.string.tab_Following)
                }
                1 -> {
                    tab.text = getString(R.string.tab_Followers)
                }
                else -> {
                    // Tambahan jika ada tab lain
                }
            }
        }
        tabLayoutMediator.attach()

        // Memantau status loading dalam ViewModel
        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        val actionBar = getSupportActionBar()

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    // Mengisi data pengguna dalam tampilan
    private fun setDetailUser(detailUserResponse: DetailUserResponse) {
        binding.tvNamaProfile.text = detailUserResponse.login
        binding.tvUsernameProfile.text = detailUserResponse.name
        binding.tvFollowing.text = "${detailUserResponse.following} Following"
        binding.tvFollowers.text = "${detailUserResponse.followers} Followers"
        Glide.with(binding.root.context)
            .load(detailUserResponse.avatarUrl)
            .circleCrop()
            .into(binding.ivProfile)
    }

    // Menampilkan atau menyembunyikan loading indicator
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarDetail.visibility = View.VISIBLE
        } else {
            binding.progressBarDetail.visibility = View.GONE
        }
    }
}
