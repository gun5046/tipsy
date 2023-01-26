package com.hanjan.hanjangame.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hanjan.hanjangame.databinding.GameListCardBinding
import com.hanjan.hanjangame.databinding.GameListDialogBinding

class GameListRecyclerViewAdapter(var host: Boolean, var activity: Activity?):
    RecyclerView.Adapter<GameListRecyclerViewAdapter.ListViewHolder>() {
    val titleList = listOf<String>("라이어 게임", "악어 이빨", "소맥 흔들기", "드래그 많이 하기", "룰렛", "순서대로 빨리 누르기")
    val contentList = listOf<String>("주제와 단어가 주어지며 단어에 대해 한 명씩 돌아가며 얘기하면서 단어가 주어지지 않는 라이어가 누군지 찾는 게임",
        "차례대로 한 명씩 이빨 하나를 누르면서 랜덤으로 걸리는 사람이 지는 게임",
        "시간 내에 누가 더 많이 흔들었는지 대결하는 게임",
        "10초 내에 가운데 원을 양쪽 끝까지 누가 더 많이 옮겼는지 대결하는 게임",
        "간단한 룰렛으로 방에 있는 사람 중 한 명 뽑기",
        "30초 내에 1 부터 15까지의 숫자를 얼마나 빠르게 누르는지 대결하는 게임")

    private lateinit var binding : GameListCardBinding

    inner class ListViewHolder(binding : GameListCardBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(title: String, content: String, position: Int){
            binding.cardTitle.text = title
            binding.cardContent.text = content
            if(!host){
                binding.startBtn.visibility = View.GONE
            } else {
                //시작 버튼
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        binding = GameListCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.onBind(titleList[position], contentList[position], position)
    }

    override fun getItemCount(): Int {
        return titleList.size
    }
}

fun showGameListDialog(context: Context){
    val builder = AlertDialog.Builder(context)
    val dialogBinding = GameListDialogBinding.inflate(LayoutInflater.from(context))
    builder.setView(dialogBinding.root)
    dialogBinding.recyclerView.adapter = GameListRecyclerViewAdapter(false, null)
    val dialog = builder.show()
    dialogBinding.closeBtn.setOnClickListener { dialog.dismiss() }
    dialogBinding.recyclerView.layoutManager = LinearLayoutManager(dialog.context)
}