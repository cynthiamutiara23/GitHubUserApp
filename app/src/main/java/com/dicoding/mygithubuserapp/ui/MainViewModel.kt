package com.dicoding.mygithubuserapp.ui

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.mygithubuserapp.data.FavoriteUserRepository
import com.dicoding.mygithubuserapp.data.local.entity.FavoriteUser
import com.dicoding.mygithubuserapp.data.remote.response.DetailUserResponse
import com.dicoding.mygithubuserapp.data.remote.response.ItemsItem
import com.dicoding.mygithubuserapp.data.remote.response.SearchResponse
import com.dicoding.mygithubuserapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val favoriteUserRepository: FavoriteUserRepository, private val pref: SettingPreferences) : ViewModel() {
    private val _user = MutableLiveData<DetailUserResponse>()
    val user: LiveData<DetailUserResponse> = _user

    private val _listUser = MutableLiveData<List<ItemsItem>>()
    val listUser: LiveData<List<ItemsItem>> = _listUser

    private val _listFollowers = MutableLiveData<List<ItemsItem>>()
    val listFollowers: LiveData<List<ItemsItem>> = _listFollowers

    private val _listFollowing = MutableLiveData<List<ItemsItem>>()
    val listFollowing: LiveData<List<ItemsItem>> = _listFollowing

    private val _favoriteUser = MutableLiveData<FavoriteUser>()
    val favoriteUser: LiveData<FavoriteUser> = _favoriteUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    companion object {
        private const val TAG = "MainViewModel"
        private const val USER_ID = "Arif"
    }

    init {
        findUser(USER_ID)
    }

    fun findUser(query: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUser(query)
        client.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    if (response.body()?.totalCount != 0) {
                        _listUser.value = response.body()?.items
                    } else {
                        _snackbarText.value = Event("Not Found")
                    }
                } else {
                    _snackbarText.value = Event("Not Found")
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                _isLoading.value = false
                _snackbarText.value = Event("Not Found")
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun findUserDetail(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUserDetail(username)
        client.enqueue(object : Callback<DetailUserResponse> {
            override fun onResponse(
                call: Call<DetailUserResponse>,
                response: Response<DetailUserResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _user.value = response.body()
                } else if (response.body()?.login == null){
                    _snackbarText.value = Event("Not Found")
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                _isLoading.value = false
                _snackbarText.value = Event("Not Found")
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun findFollowers(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getFollowers(username)
        client.enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(
                call: Call<List<ItemsItem>>,
                response: Response<List<ItemsItem>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listFollowers.value = response.body()
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun findFollowing(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getFollowing(username)
        client.enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(
                call: Call<List<ItemsItem>>,
                response: Response<List<ItemsItem>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listFollowing.value = response.body()
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getAllFavoriteUser() = favoriteUserRepository.getFavoriteUser()

    fun insert(user: FavoriteUser) {
        viewModelScope.launch {
            favoriteUserRepository.insert(user)
        }
    }

    fun delete(user: FavoriteUser) {
        viewModelScope.launch {
            favoriteUserRepository.delete(user)
        }
    }

    fun getFavoriteUserByUsername(username: String, lifecycleOwner: LifecycleOwner) {
        favoriteUserRepository.getFavoriteUserByUsername(username).observe(lifecycleOwner) { user ->
            _favoriteUser.value = user
        }
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }
}