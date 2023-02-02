package com.hanjan.hanjangame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanjan.hanjangame.adapter.GameListRecyclerViewAdapter
import com.hanjan.hanjangame.databinding.ActivityGameListBinding

class GameListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.adapter = GameListRecyclerViewAdapter(true, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
}