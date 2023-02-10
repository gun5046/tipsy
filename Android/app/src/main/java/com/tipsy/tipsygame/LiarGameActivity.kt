package com.tipsy.tipsygame

import android.animation.ObjectAnimator
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.tipsy.tipsygame.adapter.LiarGameRecyclerViewAdapter
import com.tipsy.tipsygame.adapter.showGameResultDialog
import com.tipsy.tipsygame.databinding.ActivityLiarGameBinding
import com.tipsy.tipsygame.dto.GameUserDto
import com.tipsy.tipsygame.dto.LiarGameData
import com.tipsy.tipsygame.dto.LiarGameResponse
import com.tipsy.tipsygame.dto.User
import kotlinx.coroutines.*
import org.json.JSONObject
import java.text.DecimalFormat

class LiarGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLiarGameBinding
    private lateinit var liarGameData: LiarGameData
    private var time = 6000
    private lateinit var userList: List<GameUserDto>
    var voteUser: User? = null
    private var wait: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiarGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        GlobalApplication.stompClient?.topic("/sub/play/liar-game/${GlobalApplication.roomNumber}")?.subscribe{
            val temp = it.payload.split(',')
            if(temp[0].equals("Win")){
                //라이어 승리
                runOnUiThread {
                    showGameResultDialog(this, "라이어 승리\n라이어는 ${temp[1]}님입니다.")
                    binding.waiting.visibility = View.GONE
                }
                wait?.cancel()
            } else if(temp[0].equals("Lose")){
                //라이어 패배
                runOnUiThread {
                    showGameResultDialog(this, "라이어 패배\n라이어는 ${temp[1]}님입니다.")
                    binding.waiting.visibility = View.GONE
                }
                wait?.cancel()
            } else {
                val response = jacksonObjectMapper().readValue<LiarGameResponse>(it.payload)
                liarGameData = LiarGameData(response.category, response.word, response.liar)
                userList = response.gameUserList
                val tempList = userList.toMutableList()
                var myIndex = 0
                tempList.forEachIndexed { index, gameUserDto ->
                    if(gameUserDto.nickname.equals(GlobalApplication.user.nickname)){
                        myIndex = index
                    }
                }
                tempList.removeAt(myIndex)
                userList = tempList.toList()
                runOnUiThread {
                    binding.recyclerView.adapter = LiarGameRecyclerViewAdapter(this, userList)
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                }
                CoroutineScope(Dispatchers.Main).launch {
                    binding.liarTitle.visibility = View.VISIBLE
                    ObjectAnimator.ofFloat(binding.liarTitle, "alpha", 1f, 0f).apply {
                        duration = 1000
                        start()
                    }
                    delay(1000L)
                    binding.liarBackground.visibility = View.GONE
                    val builder = AlertDialog.Builder(this@LiarGameActivity, R.style.Theme_dialog)
                    builder.setTitle("주제는 ${liarGameData.topic}입니다.")
                    binding.categoryText.text = "주제는 ${liarGameData.topic}입니다."
                    if(liarGameData.liar.equals(GlobalApplication.user.nickname)){
                        //라이어인지 구별 로그인 연동하고 나면 앱에 가지고 있는 사용자 정보와 비교할 예정
                        builder.setMessage("당신은 라이어입니다.")
                        binding.wordText.text = "당신은 라이어입니다."
                    } else {
                        builder.setMessage("단어는 ${liarGameData.word}입니다.")
                        binding.wordText.text = "단어는 ${liarGameData.word}입니다."
                    }
                    val dialog = builder.show()
                    dialog.setCancelable(false)
                    delay(3000)
                    dialog.dismiss()
                    binding.liarGameTimer.visibility = View.VISIBLE
                    binding.categoryText.visibility = View.VISIBLE
                    binding.wordText.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.IO).launch {
                        while (time>0){
                            time--
                            runOnUiThread {
                                binding.liarGameTimer.setText("${(time/100)}.${DecimalFormat("00").format((time % 100))}초")
                            }
                            delay(10L)
                        }
                        runOnUiThread {
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.voteBtn.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        sendMessage("Enter", GlobalApplication.user.nickname)
        binding.recyclerView.suppressLayout(true)
        binding.voteBtn.setOnClickListener {
            if (voteUser != null) {
                sendMessage("Vote", voteUser!!.nickname)
                wait = CoroutineScope(Dispatchers.IO).launch {
                    while (true){
                        runOnUiThread {
                            binding.liarBackground.visibility = View.VISIBLE
                            binding.waiting.visibility = View.VISIBLE
                            binding.voteBtn.visibility = View.GONE
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
            } else {
                Toast.makeText(this, "투표할 사용자를 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sendMessage(type: String, nickname: String){
        val data = JSONObject()
        data.put("type", type)
        data.put("nickname", nickname)
        GlobalApplication.stompClient?.send("/game/play/liar-game/${GlobalApplication.roomNumber}", data.toString())?.subscribe()
    }

    override fun onBackPressed() {

    }
}