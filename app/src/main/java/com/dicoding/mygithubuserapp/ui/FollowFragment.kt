package com.dicoding.mygithubuserapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.mygithubuserapp.data.remote.response.ItemsItem
import com.dicoding.mygithubuserapp.databinding.FragmentFollowBinding

class FollowFragment : Fragment() {
    private lateinit var binding: FragmentFollowBinding
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val mainViewModel by viewModels<MainViewModel>{
        ViewModelFactory.getInstance(requireActivity(), SettingPreferences.getInstance(requireContext().dataStore))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvFollow.layoutManager = layoutManager

        mainViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val position = arguments?.getInt(ARG_POSITION)
        val username = arguments?.getString(ARG_USERNAME).toString()

        if (position == 1){
            mainViewModel.findFollowers(username)
            mainViewModel.listFollowers.observe(viewLifecycleOwner) { listUser ->
                setFollow(listUser)
            }
        } else {
            mainViewModel.findFollowing(username)
            mainViewModel.listFollowing.observe(viewLifecycleOwner) { listUser ->
                setFollow(listUser)
            }
        }
    }

    companion object {
        const val ARG_POSITION = "position_number"
        const val ARG_USERNAME = "username_value"
    }

    private fun setFollow(user: List<ItemsItem>) {
        val adapter = UserAdapter(user)
        binding.rvFollow.adapter = adapter

        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ItemsItem) {
                val intentToDetail = Intent(context, DetailActivity::class.java)
                intentToDetail.putExtra("DATA", data.login)
                startActivity(intentToDetail)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}