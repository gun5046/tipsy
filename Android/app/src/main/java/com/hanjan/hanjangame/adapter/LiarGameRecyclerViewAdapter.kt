package com.hanjan.hanjangame.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanjan.hanjangame.LiarGameActivity
import com.hanjan.hanjangame.databinding.LiarGameUserItemBinding
import com.hanjan.hanjangame.dto.User

private const val TAG = "LiarGameRecyclerViewAda"

class LiarGameRecyclerViewAdapter(val activity: LiarGameActivity, val userList: List<User>) :
    RecyclerView.Adapter<LiarGameRecyclerViewAdapter.LiarGameViewHolder>() {
    val viewHolderList = mutableListOf<LiarGameViewHolder>()
    inner class LiarGameViewHolder(val binding: LiarGameUserItemBinding) : RecyclerView.ViewHolder(binding.root){
        var user = User("", "")
        fun onBind(user: User){
            this.user = user
//            binding.img
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