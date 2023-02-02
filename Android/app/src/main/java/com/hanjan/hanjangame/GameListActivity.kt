package com.hanjan.hanjangame

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanjan.hanjangame.adapter.GameListRecyclerViewAdapter
import com.hanjan.hanjangame.databinding.ActivityGameListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameListBinding
    private var host = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        host = intent.getBooleanExtra("host", false)
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
}