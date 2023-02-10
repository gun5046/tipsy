package com.tipsy.tipsygame

import android.animation.ObjectAnimator
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.tipsy.tipsygame.adapter.showGameResultDialog
import com.tipsy.tipsygame.databinding.ActivityCrocodileBinding
import com.tipsy.tipsygame.dto.CrocoDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

private const val TAG = "CrocodileActivity"

class CrocodileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCrocodileBinding
    private val toothList = mutableListOf<ImageView>()
    private val flagList = mutableListOf<Boolean>()
    private var clickable = false
    private lateinit var sp: SoundPool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrocodileBinding.inflate(layoutInflater)
        sp = SoundPool.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setMaxStreams(3)
            .build()
        val id = sp.load(this, R.raw.crocodile_sound, 0)
        GlobalApplication.stompClient?.topic("/sub/play/croco-game/${GlobalApplication.roomNumber}")?.subscribe {
            val response = jacksonObjectMapper().readValue<CrocoDto>(it.payload)
            clickable = response.nickname.equals(GlobalApplication.user.nickname)
            runOnUiThread {
                binding.turnText.text = "${response.nickname}님의 차례입니다."
            }
            if(response.type.equals("Turn")){
                flagList[response.idx] = true
                runOnUiThread {
                    ObjectAnimator.ofFloat(toothList[response.idx], "scaleY", 1f, 0.4f).apply {
                        duration = 500
                        start()
                    }
                    ObjectAnimator.ofFloat(toothList[response.idx], "translationY", 0f, 11f).apply {
                        duration = 500
                        start()
                    }
                }
            } else if(response.type.equals("Result")){
                sp.play(id, 1f, 1f, 0, 0, 1f)
                runOnUiThread {
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
                            showGameResultDialog(this@CrocodileActivity, "${response.nickname}님이 걸렸습니다")
                        }
                    }
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            binding.blackScreen.visibility = View.VISIBLE
            runOnUiThread {
                binding.crocodileTitle.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(binding.crocodileTitle, "alpha", 1f, 0f).apply {
                    duration = 1000
                    start()
                }
            }
            delay(1000)
            runOnUiThread {
                binding.blackScreen.visibility = View.GONE
            }
        }
        sendMessage("Start", 0)
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
        toothList.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                if(clickable){
                    if(!flagList[index]){
                        sendMessage("Play", index)
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        sp.release()
    }

    fun sendMessage(type: String, idx: Int){
        val data = JSONObject()
        data.put("type", type)
        data.put("nickname", GlobalApplication.user.nickname)
        data.put("idx", idx)
        GlobalApplication.stompClient?.send("/game/play/croco-game/${GlobalApplication.roomNumber}", data.toString())?.subscribe()
    }

    override fun onBackPressed() {

    }
}