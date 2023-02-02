package com.hanjan.hanjangame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hanjan.hanjangame.databinding.ActivityGameRoomBinding

class GameRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val roomNumber = intent.getStringExtra("roomNumber")
        binding.gameRoomNumberText.text = roomNumber
        binding.gameStartBtn.setOnClickListener {
            startActivity(Intent(this, GameListActivity::class.java))
        }
    }
}