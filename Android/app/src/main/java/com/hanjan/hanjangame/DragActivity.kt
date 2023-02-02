package com.hanjan.hanjangame

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.hanjan.hanjangame.adapter.showGameResultRecyclerViewDialog
import com.hanjan.hanjangame.databinding.ActivityDragBinding
import com.hanjan.hanjangame.dto.GameResult
import com.hanjan.hanjangame.dto.User
import kotlinx.coroutines.*
import java.text.DecimalFormat

class DragActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDragBinding
    private var isZero = true
    private var isMax = true
    private var count = 0
    private lateinit var timer: Job
    private var time = 1000
    private var start = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDragBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(start){
                    if (p1 == 0){
                        isZero = true
                        if(isMax){
                            count++
                            binding.dragCount.text = "Count : $count"
                            isMax = false
                        }
                    } else if(p1 == 100){
                        isMax = true
                        if(isZero){
                            count++
                            binding.dragCount.text = "Count : $count"
                            isZero = false
                        }
                    }
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        timer = CoroutineScope(Dispatchers.IO).launch {
            runOnUiThread {
                binding.dragTimer3.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(binding.dragTimer3, "alpha", 1f, 0f).apply {
                    duration = 1000
                    start()
                }
            }
            delay(1000L)
            runOnUiThread {
                binding.dragTimer3.visibility = View.GONE
                binding.dragTimer2.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(binding.dragTimer2, "alpha", 1f, 0f).apply {
                    duration = 1000
                    start()
                }
            }
            delay(1000L)
            runOnUiThread {
                binding.dragTimer2.visibility = View.GONE
                binding.dragTimer1.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(binding.dragTimer1, "alpha", 1f, 0f).apply {
                    duration = 1000
                    start()
                }
            }
            delay(1000L)
            runOnUiThread {
                binding.dragTimer1.visibility = View.GONE
                binding.dragTimerBackground.visibility = View.GONE
            }
            start = true
            while (time>0){
                time--
                runOnUiThread {
                    binding.dragTimer.setText("${(time/100)}.${DecimalFormat("00").format((time % 100))}초")
                }
                delay(10L)
            }
            start = false
            runOnUiThread{
                //서버로 데이터 보내고 결과 받을 때 까지 대기 필요
                showGameResultRecyclerViewDialog(this@DragActivity, listOf(GameResult(User("", "test"), "${count}회")))
            }
        }
    }
}