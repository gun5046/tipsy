package com.tipsy.tipsygame

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.tipsy.tipsygame.adapter.showGameResultDialog
import com.tipsy.tipsygame.adapter.showGameResultRecyclerViewDialog
import com.tipsy.tipsygame.databinding.ActivityDragBinding
import com.tipsy.tipsygame.dto.GameResult
import kotlinx.coroutines.*
import org.json.JSONObject
import java.text.DecimalFormat

private const val TAG = "DragActivity"

class DragActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDragBinding
    private var isZero = true
    private var isMax = true
    private var count = 0
    private lateinit var timer: Job
    private var time = 1000
    private var start = false
    private var wait: Job? = null
    private var countId = 0
    private var hitId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDragBinding.inflate(layoutInflater)
        setContentView(binding.root)
        countId = GlobalApplication.sp.load(this, R.raw.count, 0)
        hitId = GlobalApplication.sp.load(this, R.raw.hit, 0)
        GlobalApplication.stompClient?.topic("/sub/play/drag-game/${GlobalApplication.roomNumber}")?.subscribe {
            val temp = it.payload.split(',')
            if(temp[0].equals("ForceExit")) {
                GlobalApplication.sp.stop(countId)
                timer.cancel()
                runOnUiThread {
                    showGameResultDialog(this, "${temp[1]}님이 나갔습니다.")
                }
            } else {
                val result = jacksonObjectMapper().readValue<List<GameResult>>(it.payload)
                wait?.cancel()
                runOnUiThread {
                    binding.dragTimerBackground.visibility = View.GONE
                    binding.waiting.visibility = View.GONE
                    showGameResultRecyclerViewDialog(this, result, false)
                }
            }
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(start){
                    if (p1 == 0){
                        isZero = true
                        if(isMax){
                            GlobalApplication.sp.play(hitId, 1f, 1f, 0, 0, 1f)
                            count++
                            binding.dragCount.text = "Count : $count"
                            isMax = false
                        }
                    } else if(p1 == 100){
                        isMax = true
                        if(isZero){
                            GlobalApplication.sp.play(hitId, 1f, 1f, 0, 0, 1f)
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
                binding.dragTitle.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(binding.dragTitle, "alpha", 1f, 0f).apply {
                    duration = 1000
                    start()
                }
            }
            delay(1000L)
            GlobalApplication.sp.play(countId, 1f, 1f, 0, 0, 1f)
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
                sendResult()
                binding.dragTimerBackground.visibility = View.VISIBLE
                binding.waiting.visibility = View.VISIBLE
                wait = CoroutineScope(Dispatchers.IO).launch {
                    while (true){
                        runOnUiThread {
                            ObjectAnimator.ofFloat(binding.waiting, "alpha", 1f, 0f).apply {
                                duration = 500
                                start()
                            }
                        }
                        delay(500)
                        runOnUiThread {
                            ObjectAnimator.ofFloat(binding.waiting, "alpha", 0f, 1f).apply {
                                duration = 500
                                start()
                            }
                        }
                        delay(500)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {

    }

    fun sendResult(){
        val data = JSONObject()
        data.put("nickname", GlobalApplication.user.nickname)
        data.put("image", GlobalApplication.user.img)
        data.put("score", count)
        GlobalApplication.stompClient?.send("/game/play/drag-game/${GlobalApplication.roomNumber}", data.toString())?.subscribe()
    }
}