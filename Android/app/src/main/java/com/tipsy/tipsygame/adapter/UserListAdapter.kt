package com.tipsy.tipsygame.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tipsy.tipsygame.R
import com.tipsy.tipsygame.databinding.UserListItemBinding
import com.tipsy.tipsygame.dto.GameUserDto

class UserListAdapter(val userList: List<GameUserDto>) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {
    private lateinit var binding: UserListItemBinding

    inner class UserViewHolder(binding: UserListItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(user: GameUserDto){
//            binding.userImg 이미지 교체 필요
            Glide.with(binding.root)
                .load(user.img)
                .error(R.drawable.ic_baseline_videogame_asset_24).into(binding.userImg)
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