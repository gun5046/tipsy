package com.hanjan.hanjangame

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanjan.hanjangame.adapter.LiarGameRecyclerViewAdapter
import com.hanjan.hanjangame.adapter.showGameResultDialog
import com.hanjan.hanjangame.databinding.ActivityLiarGameBinding
import com.hanjan.hanjangame.dto.LiarGameData
import com.hanjan.hanjangame.dto.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class LiarGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLiarGameBinding
    private var liarGameData = LiarGameData("과일", "사과", User("", "test"))
    private var time = 100
    private var userList = listOf(User("", "test"), User("", "test1"), User("", "test2"))
    var voteUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiarGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.adapter = LiarGameRecyclerViewAdapter(this, userList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.Main).launch {
            val builder = AlertDialog.Builder(this@LiarGameActivity)
            builder.setTitle("주제는 ${liarGameData.topic}입니다.")
            if(liarGameData.liar.nickname == ""){
                //라이어인지 구별 로그인 연동하고 나면 앱에 가지고 있는 사용자 정보와 비교할 예정
                builder.setMessage("당신은 라이어입니다.")
            } else {
                builder.setMessage("단어는 ${liarGameData.word}입니다.")
            }
            val dialog = builder.show()
            dialog.setCancelable(false)
            delay(3000)
            dialog.dismiss()
            while (time>0){
                time--
                runOnUiThread {
                    binding.liarGameTimer.setText("${(time/100)}.${DecimalFormat("00").format((time % 100))}초")
                }
                delay(10L)
            }
            binding.recyclerView.visibility = View.VISIBLE
            binding.voteBtn.visibility = View.VISIBLE
        }
        binding.recyclerView.suppressLayout(true)
        binding.voteBtn.setOnClickListener {
            if (voteUser != null) {
                //추후 결과 받으면 판단해서 결과 다시 띄울 예정
                if(liarGameData.liar.nickname.equals(voteUser!!.nickname)){
                    showGameResultDialog(this, "라이어 패배")
                } else {
                    showGameResultDialog(this, "라이어 승리")
                }
            } else {
                Toast.makeText(this, "투표할 사용자를 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}