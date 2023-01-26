package com.hanjan.hanjangame.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class GameListRecyclerViewAdapter(): RecyclerView.Adapter<GameListRecyclerViewAdapter.ListViewHolder>() {
    val titleList = listOf<String>("라이어 게임", "악어 이빨", "소맥 흔들기", "드래그 많이 하기", "룰렛", "순서대로 빨리 누르기")
    val contentList = listOf<String>("주제와 단어가 주어지며 단어에 대해 한 명씩 돌아가며 얘기하면서 단어가 주어지지 않는 라이어가 누군지 찾는 게임",
        "차례대로 한 명씩 이빨 하나를 누르면서 랜덤으로 걸리는 사람이 지는 게임",
        "시간 내에 누가 더 많이 흔들었는지 대결하는 게임",
        "10초 내에 가운데 원을 양쪽 끝까지 누가 더 많이 옮겼는지 대결하는 게임",
        "간단한 룰렛으로 방에 있는 사람 중 한 명 뽑기",
        "30초 내에 1 부터 15까지의 숫자를 얼마나 빠르게 누르는지 대결하는 게임")

    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}