package com.tipsy.tipsygame.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tipsy.tipsygame.LiarGameActivity
import com.tipsy.tipsygame.R
import com.tipsy.tipsygame.databinding.LiarGameUserItemBinding
import com.tipsy.tipsygame.dto.User

private const val TAG = "LiarGameRecyclerViewAda"

class LiarGameRecyclerViewAdapter(val activity: LiarGameActivity, val userList: List<User>) :
    RecyclerView.Adapter<LiarGameRecyclerViewAdapter.LiarGameViewHolder>() {
    val viewHolderList = mutableListOf<LiarGameViewHolder>()
    inner class LiarGameViewHolder(val binding: LiarGameUserItemBinding) : RecyclerView.ViewHolder(binding.root){
        var user = User("", "")
        fun onBind(user: User){
            this.user = user
            Glide.with(binding.root)
                .load(user.img)
                .error(R.drawable.ic_baseline_videogame_asset_24).into(binding.img)
            binding.nickname.text = user.nickname
            binding.root.setOnClickListener {
                activity.voteUser = user
                changeBackgroundColor()
                viewHolderList.forEach {
                    it.changeBackgroundColor()
                }
            }
        }

        fun changeBackgroundColor(){
            if(activity.voteUser == user){
                binding.root.setBackgroundColor(Color.GRAY)
            } else {
                binding.root.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiarGameViewHolder {
        val binding = LiarGameUserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = LiarGameViewHolder(binding)
        viewHolderList.add(viewHolder)
        return viewHolder
    }

    override fun onBindViewHolder(holder: LiarGameViewHolder, position: Int) {
        holder.onBind(userList[position])
    }

    override fun getItemCount(): Int {
        return userList.size
    }


}