package com.dicoding.mygithubuserapp.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dicoding.mygithubuserapp.R
import com.dicoding.mygithubuserapp.data.local.entity.FavoriteUser
import com.dicoding.mygithubuserapp.data.remote.response.DetailUserResponse
import com.dicoding.mygithubuserapp.databinding.ActivityDetailBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity() {
    private var favoriteUser: FavoriteUser? = null
    private var isFavoriteUser: Boolean = false
    private lateinit var binding: ActivityDetailBinding
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val mainViewModel by viewModels<MainViewModel>(){
        ViewModelFactory.getInstance(application, SettingPreferences.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.action_bar_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val data = intent.getStringExtra("DATA").toString()

        mainViewModel.findUserDetail(data)

        mainViewModel.user.observe(this) { user ->
            setUserDetail(user)
        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        sectionsPagerAdapter.username = data
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
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

    private fun setUserDetail(user: DetailUserResponse) {
        binding.apply {
            Glide.with(this@DetailActivity).load(user.avatarUrl).into(photo)
            name.text = user.name
            username.text = user.login
            followersCount.text = resources.getString(R.string.followers_count, user.followers)
            followingCount.text = resources.getString(R.string.following_count, user.following)
        }

        mainViewModel.getFavoriteUserByUsername(user.login, this)
        mainViewModel.favoriteUser.observe(this) {favUser ->
            if (favUser != null) {
                favoriteUser = favUser
                isFavoriteUser = true
                binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorited_white))
            }
        }

        binding.fabFavorite.setOnClickListener { view ->
            if (view.id == R.id.fab_favorite) {
                if (favoriteUser == null) {
                    favoriteUser = FavoriteUser().apply {
                        username = user.login
                        avatarUrl = user.avatarUrl
                    }
                    mainViewModel.insert(favoriteUser!!)
                    isFavoriteUser = true
                    binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorited_white))
                    showToast(getString(R.string.favorite_saved))
                } else {
                    mainViewModel.delete(favoriteUser!!)
                    favoriteUser = null
                    isFavoriteUser = false
                    binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white))
                    showToast(getString(R.string.favorite_deleted))
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_followers,
            R.string.tab_following
        )
    }
}