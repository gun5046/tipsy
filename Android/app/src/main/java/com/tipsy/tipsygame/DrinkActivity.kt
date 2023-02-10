package com.tipsy.tipsygame

import android.animation.ObjectAnimator
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.tipsy.tipsygame.adapter.showGameResultDialog
import com.tipsy.tipsygame.adapter.showGameResultRecyclerViewDialog
import com.tipsy.tipsygame.databinding.ActivityDrinkBinding
import com.tipsy.tipsygame.dto.GameResult
import kotlinx.coroutines.*
import org.json.JSONObject
import java.text.DecimalFormat

private const val TAG = "DrinkActivity"

class DrinkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDrinkBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor
    private var lastX : Float? = 0.0f
    private var lastY : Float? = 9.8f
    private var lastZ : Float? = 0.0f
    private var lastTime = System.currentTimeMillis()
    private var count = 0
    private lateinit var timer: Job
    private var time = 1000
    private var check = false
    private var countId = 0
    private var wait: Job? = null
    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {
            if(p0?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION){
                Log.d(TAG, "x: ${p0?.values?.get(0)} y: ${p0?.values?.get(1)} z: ${p0?.values?.get(2)}")
                var currentTime = System.currentTimeMillis()
                val gap = currentTime - lastTime
                if(gap > 100){
                    lastTime = currentTime
                    var x = p0?.values?.get(0)
                    var y = p0?.values?.get(1)
                    var z = p0?.values?.get(2)

                    val speed = Math.abs(y!! - lastY!!)

                    if(Math.abs(y!!) > 12){
                        check = true
                    }

                    if(check && speed > 21){
                        check = false
                        count++
                        binding.count.text = "횟수 : $count"
                    }

                    lastX = p0?.values?.get(0)
                    lastY = p0?.values?.get(1)
                    lastZ = p0?.values?.get(2)
                }
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrinkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        countId = GlobalApplication.sp.load(this, R.raw.count, 0)
        GlobalApplication.stompClient?.topic("/sub/play/drink-game/${GlobalApplication.roomNumber}")?.subscribe {
            val temp = it.payload.split(',')
            if(temp[0].equals("ForceExit")) {
                GlobalApplication.sp.stop(countId)
                timer.cancel()
                if(sensorManager != null){
                    sensorManager.unregisterListener(sensorEventListener, sensor)
                }
                runOnUiThread {
                    showGameResultDialog(this, "${temp[1]}님이 나갔습니다.")
                }
            } else {
                val result = jacksonObjectMapper().readValue<List<GameResult>>(it.payload)
                wait?.cancel()
                runOnUiThread {
                    binding.drinkTimerBackground.visibility = View.GONE
                    binding.waiting.visibility = View.GONE
                    showGameResultRecyclerViewDialog(this, result, false)
                }
            }
        }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        timer = CoroutineScope(Dispatchers.IO).launch {
            runOnUiThread {
                binding.drinkTitle.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(binding.drinkTitle, "alpha", 1f, 0f).apply {
                    duration = 1000
                    start()
                }
            }
            delay(1000L)
            GlobalApplication.sp.play(countId, 1f, 1f, 0, 0, 1f)
            runOnUiThread {
                binding.drinkTimer3.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(binding.drinkTimer3, "alpha", 1f, 0f).apply {
                    duration = 1000
                    start()
                }
            }
            delay(1000L)
            runOnUiThread {
                binding.drinkTimer3.visibility = View.GONE
                binding.drinkTimer2.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(binding.drinkTimer2, "alpha", 1f, 0f).apply {
                    duration = 1000
                    start()
                }
            }
            delay(1000L)
            runOnUiThread {
                binding.drinkTimer2.visibility = View.GONE
                binding.drinkTimer1.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(binding.drinkTimer1, "alpha", 1f, 0f).apply {
                    duration = 1000
                    start()
                }
            }
            delay(1000L)
            runOnUiThread {
                binding.drinkTimer1.visibility = View.GONE
                binding.drinkTimerBackground.visibility = View.GONE
            }
            if(sensor != null){
                sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
            while (time>0){
                time--
                runOnUiThread {
                    binding.drinkTimer.setText("${(time/100)}.${DecimalFormat("00").format((time % 100))}초")
                }
                delay(10L)
            }
            if(sensorManager != null){
                sensorManager.unregisterListener(sensorEventListener, sensor)
            }
            runOnUiThread {
                sendResult()
                binding.drinkTimerBackground.visibility = View.VISIBLE
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
        val beer = CoroutineScope(Dispatchers.IO).launch {
            while (true){
                runOnUiThread {
                    ObjectAnimator.ofFloat(binding.beer, "translationY", -100f, 100f).apply {
                        duration = 200
                        start()
                    }
                }
                delay(200)
                runOnUiThread {
                    ObjectAnimator.ofFloat(binding.beer, "translationY", 100f, -100f).apply {
                        duration = 200
                        start()
                    }
                }
                delay(200)
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
        GlobalApplication.stompClient?.send("/game/play/drink-game/${GlobalApplication.roomNumber}", data.toString())?.subscribe()
    }
}