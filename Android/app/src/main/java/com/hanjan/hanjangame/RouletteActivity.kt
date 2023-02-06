package com.hanjan.hanjangame

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bluehomestudio.luckywheel.WheelItem
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hanjan.hanjangame.adapter.showGameResultDialog
import com.hanjan.hanjangame.databinding.ActivityRouletteBinding
import com.hanjan.hanjangame.dto.GameUserDto
import com.hanjan.hanjangame.dto.RouletteResponseDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RouletteActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRouletteBinding
    private val colorList = listOf(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE)
    private var point = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouletteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val wheelItems = mutableListOf<WheelItem>()
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        GlobalApplication.stompClient?.topic("/sub/play/roulette-game/${GlobalApplication.roomNumber}")?.subscribe {
            val response = jacksonObjectMapper().readValue<RouletteResponseDto>(it.payload)
            response.list.forEachIndexed { index, gameUserDto ->
                wheelItems.add(WheelItem(colorList[index], bitmap, gameUserDto.nickname))
            }
            point = response.index
            CoroutineScope(Dispatchers.IO).launch {
                runOnUiThread {
                    ObjectAnimator.ofFloat(binding.rouletteTitle, "alpha", 1f, 0f).apply {
                        duration = 1000
                        start()
                    }
                }
                delay(1000)
                runOnUiThread {
                    binding.timerBackground.visibility = View.GONE
                    binding.luckyWheel.addWheelItems(wheelItems)
                    binding.luckyWheel.rotateWheelTo(response.index)
                    binding.luckyWheel.setLuckyWheelReachTheTarget {
                        GlobalApplication.stompClient?.send("/game/play/roulette-game/${GlobalApplication.roomNumber}", wheelItems[point - 1].text)?.subscribe()
                        showGameResultDialog(this@RouletteActivity, "${ wheelItems[point - 1].text } 당첨")
                    }
                }
            }
        }
        GlobalApplication.stompClient?.send("/game/play/roulette-game/${GlobalApplication.roomNumber}", "Enter")?.subscribe()
        //사용자 리스트를 받게 될 시 색을 룰렛 칸마다 색을 어떻게 넣을지 생각해야함
    }
}