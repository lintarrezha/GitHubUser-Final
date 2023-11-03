package com.dicoding.githubuser.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.response.ItemsItem
import com.dicoding.githubuser.databinding.ActivityMainBinding
import com.dicoding.githubuser.helper.DarkmodeViewModelFactory
import com.dicoding.githubuser.detail.UserAdapter
import com.dicoding.githubuser.ui.darkmode.DarkModeActivity
import com.dicoding.githubuser.ui.darkmode.DarkmodeViewModel
import com.dicoding.githubuser.ui.darkmode.SettingPreferences
import com.dicoding.githubuser.ui.darkmode.dataStore
import com.dicoding.githubuser.ui.favorite.FavoriteUserActivity
import com.dicoding.githubuser.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Inisialisasi Preferences untuk Dark Mode
        val pref = SettingPreferences.getInstance(application.dataStore)
        val darkModeViewModel = ViewModelProvider(this, DarkmodeViewModelFactory(pref))[DarkmodeViewModel::class.java]

        // perubahan tema (Light/Dark Mode)
        darkModeViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Setup Search Bar Menu
        binding.searchBar.inflateMenu(R.menu.option_menu)
        binding.searchBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu1 -> {
                    startActivity(Intent(this@MainActivity, FavoriteUserActivity::class.java))
                    true
                }
                R.id.menu2 -> {
                    startActivity(Intent(this@MainActivity, DarkModeActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Inisialisasi ViewModel
        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]

        // Setup RecyclerView
        val layoutManager = LinearLayoutManager(this)
        binding.rvUser.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvUser.addItemDecoration(itemDecoration)

        // daftar pengguna dari ViewModel
        mainViewModel.listUser.observe(this) { items ->
            setListUser(items)
        }

        // status loading dari ViewModel
        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { textView, actionId, event ->
                    val query = searchView.text.toString()
                    //searchBar.text = searchView.text //mengambil inputan dari searchbar
                    mainViewModel.findItemsitem(query)
                    searchView.hide()
                    Toast.makeText(this@MainActivity, searchView.text, Toast.LENGTH_SHORT).show()
                    false
                }
        }
    }

    private fun setListUser(items: List<ItemsItem>){
        // Mengatur daftar pengguna ke adapter
        val adapter = UserAdapter()
        adapter.submitList(items)
        binding.rvUser.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean){
        // Menampilkan atau menyembunyikan ProgressBar
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
