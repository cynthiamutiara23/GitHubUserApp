package com.dicoding.mygithubuserapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.mygithubuserapp.data.Result
import com.dicoding.mygithubuserapp.data.remote.response.ItemsItem
import com.dicoding.mygithubuserapp.databinding.ActivityFavoriteUserBinding

class FavoriteUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteUserBinding
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val mainViewModel by viewModels<MainViewModel>(){
        ViewModelFactory.getInstance(application, SettingPreferences.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Favorite User"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.rvFavUser.layoutManager = LinearLayoutManager(this)

        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        mainViewModel.getAllFavoriteUser().observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val users = result.data
                    val items = arrayListOf<ItemsItem>()
                    users.map {
                        val item = ItemsItem(login = it.username, avatarUrl = it.avatarUrl ?: "")
                        items.add(item)
                    }

                    val adapter = UserAdapter(items)
                    binding.rvFavUser.adapter = adapter

                    adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
                        override fun onItemClicked(data: ItemsItem) {
                            val intentToDetail = Intent(this@FavoriteUserActivity, DetailActivity::class.java)
                            intentToDetail.putExtra("DATA", data.login)
                            startActivity(intentToDetail)
                        }
                    })
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Terjadi kesalahan" + result.error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}