package com.hanjan.hanjangame

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.hanjan.hanjangame.adapter.showGameResultRecyclerViewDialog
import com.hanjan.hanjangame.databinding.ActivityOrderingBinding
import com.hanjan.hanjangame.dto.GameResult
import com.hanjan.hanjangame.dto.User
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.util.*

class OrderingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderingBinding
    private lateinit var timer: Job
    private var time = 3000
    private val list = mutableListOf<Int>()
    private val btnList = mutableListOf<Button>()
    private var count = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initList()
        initButtonList()
        randomList()
        changeButtonText()
        buttonSetOnClick()
        timer = CoroutineScope(Dispatchers.IO).launch {
            runOnUiThread {
                binding.timer3.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(binding.timer3, "alpha", 1f, 0f).apply {
                    duration = 1000
                    start()
                }
            }
            delay(1000L)
            runOnUiThread {
                binding.timer3.visibility = View.GONE
                binding.timer2.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(binding.timer2, "alpha", 1f, 0f).apply {
                    duration = 1000
                    start()
                }
            }
            delay(1000L)
            runOnUiThread {
                binding.timer2.visibility = View.GONE
                binding.timer1.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(binding.timer1, "alpha", 1f, 0f).apply {
                    duration = 1000
                    start()
                }
            }
            delay(1000L)
            runOnUiThread {
                binding.timer1.visibility = View.GONE
                binding.timerBackground.visibility = View.GONE
            }
            while (time>0){
                time--
                runOnUiThread {
                    binding.timer.setText("${(time/100)}.${DecimalFormat("00").format((time % 100))}초")
                }
                delay(10L)
            }
            runOnUiThread {
                //서버에 데이터 보내고 다른 데이터 받을 때 까지 대기 필요
                showGameResultRecyclerViewDialog(this@OrderingActivity, listOf(GameResult(User("", "test"), "실패")))
            }
        }
    }

    fun initList(){
        for(i in 1..15){
            list.add(i)
        }
    }

    fun initButtonList(){
        btnList.add(binding.btn1)
        btnList.add(binding.btn2)
        btnList.add(binding.btn3)
        btnList.add(binding.btn4)
        btnList.add(binding.btn5)
        btnList.add(binding.btn6)
        btnList.add(binding.btn7)
        btnList.add(binding.btn8)
        btnList.add(binding.btn9)
        btnList.add(binding.btn10)
        btnList.add(binding.btn11)
        btnList.add(binding.btn12)
        btnList.add(binding.btn13)
        btnList.add(binding.btn14)
        btnList.add(binding.btn15)
    }

    fun randomList(){
        for(i in 0..100){
            var random = Random().nextInt(list.size)
            var temp = list[random]
            list.remove(temp)
            list.add(temp)
        }
    }

    fun changeButtonText(){
        for(i in 0..14){
            btnList[i].text = "${list[i]}"
        }
    }

    fun buttonSetOnClick(){
        for(i in 0..14){
            btnList[i].setOnClickListener {
                if(count == btnList[i].text.toString().toInt()){
                    if(count == 15){
                        timer.cancel()
                        //서버에 데이터 보내고 다른 데이터 받을 때 까지 대기 필요
                        showGameResultRecyclerViewDialog(this@OrderingActivity, listOf(GameResult(User("", "test"), binding.timer.text.toString())))
                    }
                    count++
//                    btnList[i].visibility = View.INVISIBLE
                    ObjectAnimator.ofFloat(btnList[i], "scaleX", 1.0f, 0f).apply {
                        duration = 500
                        start()
                    }
                    ObjectAnimator.ofFloat(btnList[i], "scaleY", 1.0f, 0f).apply {
                        duration = 500
                        start()
                    }
                }
            }
        }
    }
}