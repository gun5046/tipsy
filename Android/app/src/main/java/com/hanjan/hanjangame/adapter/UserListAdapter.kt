package com.hanjan.hanjangame.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanjan.hanjangame.databinding.UserListItemBinding
import com.hanjan.hanjangame.dto.UserWithData

class UserListAdapter(val userList: List<UserWithData>) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {
    private lateinit var binding: UserListItemBinding

    inner class UserViewHolder(binding: UserListItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(user: UserWithData){
//            binding.userImg 이미지 교체 필요
            binding.userNickname.text = user.nickname
            if(!user.host){
                binding.userHost.text = "Ready"
                if(user.ready){
                    binding.userHost.setTextColor(Color.BLUE)
                } else {
                    binding.userHost.setTextColor(Color.GRAY)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        binding = UserListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.onBind(userList[position])
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}