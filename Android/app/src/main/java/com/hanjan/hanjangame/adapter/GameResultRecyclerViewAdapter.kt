package com.hanjan.hanjangame.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hanjan.hanjangame.databinding.GameResultDialogBinding
import com.hanjan.hanjangame.databinding.GameResultItemBinding
import com.hanjan.hanjangame.databinding.GameResultListDialogBinding
import com.hanjan.hanjangame.dto.GameResult
import kotlinx.coroutines.*

private const val TAG = "GameResultRecyclerViewA"

class GameResultRecyclerViewAdapter(val gameResultList: List<GameResult>) :
    RecyclerView.Adapter<GameResultRecyclerViewAdapter.GameResultViewHolder>() {
    inner class GameResultViewHolder(val binding: GameResultItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(result: GameResult){
            //binding.userImg
            binding.userNickname.text = result.nickname
            binding.userScore.text = result.score
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameResultViewHolder {
        return GameResultViewHolder(GameResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: GameResultViewHolder, position: Int) {
        holder.onBind(gameResultList[position])
    }

    override fun getItemCount(): Int {
        return gameResultList.size
    }
}

fun showGameResultRecyclerViewDialog(activity: Activity, resultList: List<GameResult>){
    val builder = AlertDialog.Builder(activity)
    val binding = GameResultListDialogBinding.inflate(LayoutInflater.from(activity))
    builder.setView(binding.root)
    binding.recyclerView.adapter = GameResultRecyclerViewAdapter(resultList)
    binding.recyclerView.layoutManager = LinearLayoutManager(activity)
    val dialog = builder.show()
    dialog.setCancelable(false)
    CoroutineScope(Dispatchers.IO).launch {
        delay(3000)
        activity.runOnUiThread {
            dialog.dismiss()
        }
        activity.finish()
    }
}

fun showGameResultDialog(activity: Activity, result: String){
    val builder = AlertDialog.Builder(activity)
    val binding = GameResultDialogBinding.inflate(LayoutInflater.from(activity))
    builder.setView(binding.root)
    binding.gameResultText.text = result
    val dialog = builder.show()
    dialog.setCancelable(false)
    CoroutineScope(Dispatchers.IO).launch {
        delay(3000)
        activity.runOnUiThread {
            dialog.dismiss()
            activity.finish()
        }
    }
}