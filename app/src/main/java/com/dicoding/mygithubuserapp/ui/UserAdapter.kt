package com.dicoding.mygithubuserapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.mygithubuserapp.data.remote.response.ItemsItem
import com.dicoding.mygithubuserapp.databinding.ItemUserBinding

class UserAdapter (private var listUser: List<ItemsItem>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemUserBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.binding.tvItemName.text = listUser[position].login
        Glide.with(viewHolder.itemView.context)
            .load(listUser[position].avatarUrl)
            .into(viewHolder.binding.imgItemPhoto)
        viewHolder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listUser[viewHolder.adapterPosition]) }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ItemsItem)
    }

    override fun getItemCount() = listUser.size

    class ViewHolder(var binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)
}