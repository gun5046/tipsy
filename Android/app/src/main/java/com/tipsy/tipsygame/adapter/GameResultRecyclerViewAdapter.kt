package com.tipsy.tipsygame.adapter

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tipsy.tipsygame.R
import com.tipsy.tipsygame.databinding.GameResultDialogBinding
import com.tipsy.tipsygame.databinding.GameResultItemBinding
import com.tipsy.tipsygame.databinding.GameResultListDialogBinding
import com.tipsy.tipsygame.dto.GameResult
import kotlinx.coroutines.*
import java.text.DecimalFormat

private const val TAG = "GameResultRecyclerViewA"

class GameResultRecyclerViewAdapter(val gameResultList: List<GameResult>, val time: Boolean) :
    RecyclerView.Adapter<GameResultRecyclerViewAdapter.GameResultViewHolder>() {
    inner class GameResultViewHolder(val binding: GameResultItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(result: GameResult){
            Glide.with(binding.root)
                .load(result.image)
                .error(R.drawable.ic_baseline_videogame_asset_24).into(binding.userImg)
            binding.userNickname.text = result.nickname
            if (time){
                binding.userScore.text = "${(result.score/100)}.${DecimalFormat("00").format((result.score % 100))}초"
            } else {
                binding.userScore.text = "${result.score}점"
            }
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

fun showGameResultRecyclerViewDialog(activity: Activity, resultList: List<GameResult>, time: Boolean){
    val builder = AlertDialog.Builder(activity, R.style.Theme_dialog)
    val binding = GameResultListDialogBinding.inflate(LayoutInflater.from(activity))
    builder.setView(binding.root)
    binding.recyclerView.suppressLayout(true)
    binding.recyclerView.adapter = GameResultRecyclerViewAdapter(resultList, time)
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
    val builder = AlertDialog.Builder(activity, R.style.Theme_dialog)
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