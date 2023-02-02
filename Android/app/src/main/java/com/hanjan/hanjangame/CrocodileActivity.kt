package com.hanjan.hanjangame

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.hanjan.hanjangame.adapter.showGameResultDialog
import com.hanjan.hanjangame.databinding.ActivityCrocodileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.random.nextInt

private const val TAG = "CrocodileActivity"

class CrocodileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCrocodileBinding
    private val toothList = mutableListOf<ImageView>()
    private val flagList = mutableListOf<Boolean>()
    private var condition = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrocodileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toothList.add(binding.teeth0)
        toothList.add(binding.teeth1)
        toothList.add(binding.teeth2)
        toothList.add(binding.teeth3)
        toothList.add(binding.teeth4)
        toothList.add(binding.teeth5)
        toothList.add(binding.teeth6)
        toothList.add(binding.teeth7)
        toothList.add(binding.teeth8)
        toothList.add(binding.teeth9)
        toothList.add(binding.teeth10)
        toothList.add(binding.teeth11)
        toothList.add(binding.teeth12)
        toothList.add(binding.teeth13)
        toothList.add(binding.teeth14)
        toothList.add(binding.teeth15)
        toothList.add(binding.teeth16)
        for(i in 0..16){
            flagList.add(false)
        }
        condition = Random.nextInt(0..16)
        Log.d(TAG, "onCreate: $condition")
        toothList.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                if(!flagList[index]){
                    flagList[index] = true
                    ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 0.4f).apply {
                        duration = 500
                        start()
                    }
                    ObjectAnimator.ofFloat(imageView, "translationY", 0f, 11f).apply {
                        duration = 500
                        start()
                    }
                    if(index == condition){
                        //걸린 사람만 애니메이션과 소리 나면서 결과창
                        //걸리지 않은 사람들은 페이드아웃 효과 주고 결과창
                        binding.aligatorGameOver.visibility = View.VISIBLE
                        ObjectAnimator.ofFloat(binding.aligatorGameOver, "scaleX", 1f, 10f).apply {
                            duration = 500
                            start()
                        }
                        ObjectAnimator.ofFloat(binding.aligatorGameOver, "scaleY", 1f, 10f).apply {
                            duration = 500
                            start()
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            delay(250)
                            runOnUiThread {
                                binding.blackScreen.visibility = View.VISIBLE
                                ObjectAnimator.ofFloat(binding.blackScreen, "alpha", 0f, 1f).apply {
                                    duration = 500
                                    start()
                                }
                            }
                            delay(500)
                            runOnUiThread {
                                showGameResultDialog(this@CrocodileActivity, "test님이 걸렸습니다")
                            }
                        }
                    }
                }
            }
        }
    }
}