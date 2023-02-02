package com.hanjan.hanjangame

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
import com.hanjan.hanjangame.adapter.showGameResultRecyclerViewDialog
import com.hanjan.hanjangame.databinding.ActivityDrinkBinding
import com.hanjan.hanjangame.dto.GameResult
import com.hanjan.hanjangame.dto.User
import kotlinx.coroutines.*
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
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        timer = CoroutineScope(Dispatchers.IO).launch {
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
            runOnUiThread{
                //서버로 데이터 보내고 결과 받을 때 까지 대기 필요
                showGameResultRecyclerViewDialog(this@DrinkActivity, listOf(GameResult(User("", "test"), "${count}회")))
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
}