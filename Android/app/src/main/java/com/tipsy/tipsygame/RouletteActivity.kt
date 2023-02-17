package com.tipsy.tipsygame

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bluehomestudio.luckywheel.WheelItem
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.tipsy.tipsygame.adapter.showGameResultDialog
import com.tipsy.tipsygame.databinding.ActivityRouletteBinding
import com.tipsy.tipsygame.dto.RouletteResponseDto
import kotlinx.coroutines.*

class RouletteActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRouletteBinding
    private val colorList = listOf(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE)
    private var point = 3
    private lateinit var roulette: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouletteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sound = GlobalApplication.sp.load(this, R.raw.roulette, 0)
        val wheelItems = mutableListOf<WheelItem>()
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        GlobalApplication.stompClient?.topic("/sub/play/roulette-game/${GlobalApplication.roomNumber}")?.subscribe {
            val temp = it.payload.split(',')
            if(temp[0].equals("ForceExit")) {
                GlobalApplication.sp.stop(sound)
                roulette.cancel()
                runOnUiThread {
                    showGameResultDialog(this, "${temp[1]}님이 나갔습니다.")
                }
            } else {
                val response = jacksonObjectMapper().readValue<RouletteResponseDto>(it.payload)
                response.list.forEachIndexed { index, gameUserDto ->
                    wheelItems.add(WheelItem(colorList[index], bitmap, gameUserDto.nickname))
                }
                point = response.index
                roulette = CoroutineScope(Dispatchers.IO).launch {
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
                        GlobalApplication.sp.play(sound, 1f, 1f, 0, 0, 0.7f)
                        binding.luckyWheel.rotateWheelTo(response.index)
                    }
                    delay(9000)
                    runOnUiThread {
                        binding.luckyWheel.setLuckyWheelReachTheTarget {
                            GlobalApplication.stompClient?.send("/game/play/roulette-game/${GlobalApplication.roomNumber}", wheelItems[point - 1].text)?.subscribe()
                            showGameResultDialog(this@RouletteActivity, "${ wheelItems[point - 1].text } 당첨")
                        }
                    }
                }
            }
        }
        GlobalApplication.stompClient?.send("/game/play/roulette-game/${GlobalApplication.roomNumber}", "Enter")?.subscribe()
        //사용자 리스트를 받게 될 시 색을 룰렛 칸마다 색을 어떻게 넣을지 생각해야함
    }

    override fun onBackPressed() {

    }
}