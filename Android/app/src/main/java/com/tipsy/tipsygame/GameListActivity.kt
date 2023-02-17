package com.tipsy.tipsygame

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tipsy.tipsygame.adapter.GameListRecyclerViewAdapter
import com.tipsy.tipsygame.adapter.showGameResultDialog
import com.tipsy.tipsygame.databinding.ActivityGameListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameListBinding
    private var host = false
    val activityList = listOf(
        LiarGameActivity::class.java,
        CrocodileActivity::class.java,
        DrinkActivity::class.java,
        DragActivity::class.java,
        RouletteActivity::class.java,
        OrderingActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        GlobalApplication.gid = -1
        host = intent.getBooleanExtra("host", false)
        GlobalApplication.stompClient?.topic("/sub/select/${GlobalApplication.roomNumber}")?.subscribe {
            val temp = it.payload.split(',')
            if(temp[0].equals("ForceExit")){
                runOnUiThread {
                    showGameResultDialog(this, "${temp[1]}님이 나갔습니다.")
                }
            } else {
                GlobalApplication.gid = it.payload.toInt()
                val idx = it.payload.toInt() - 1
                val intent = Intent(this, activityList[idx])
                startActivity(intent)
                finish()
            }
        }
        if(host){
            binding.gameListWaitText.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = GameListRecyclerViewAdapter(host, this)
        } else {
            //텍스트에 fade-in, fade-out 애니메이션 넣어서 자연스럽게 처리
            CoroutineScope(Dispatchers.IO).launch {
                while (true){
                    runOnUiThread {
                        ObjectAnimator.ofFloat(binding.gameListWaitText, "alpha", 1f, 0f).apply {
                            duration = 500
                            start()
                        }
                    }
                    delay(500)
                    runOnUiThread {
                        ObjectAnimator.ofFloat(binding.gameListWaitText, "alpha", 0f, 1f).apply {
                            duration = 500
                            start()
                        }
                    }
                    delay(500)
                }
            }
            binding.gameListWaitText.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }
    }

    override fun onBackPressed() {

    }
}