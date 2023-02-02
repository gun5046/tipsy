package com.hanjan.hanjangame

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bluehomestudio.luckywheel.WheelItem
import com.hanjan.hanjangame.databinding.ActivityRouletteBinding

class RouletteActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRouletteBinding

    private var point = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouletteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val wheelItems = mutableListOf<WheelItem>()
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        //사용자 리스트를 받게 될 시 색을 룰렛 칸마다 색을 어떻게 넣을지 생각해야함
        wheelItems.add(WheelItem(Color.RED, bitmap, "닉네임"))
        wheelItems.add(WheelItem(Color.BLUE, bitmap, "2"))
        wheelItems.add(WheelItem(Color.MAGENTA, bitmap, "test"))
        wheelItems.add(WheelItem(Color.GREEN, bitmap, "4"))
        wheelItems.add(WheelItem(Color.LTGRAY, bitmap, "5"))
        wheelItems.add(WheelItem(Color.CYAN, bitmap, "6"))
        binding.luckyWheel.addWheelItems(wheelItems)
        binding.spinBtn.setOnClickListener {
            //데이터에서 사용자 이름을 추출해 인덱스를 뽑아야 함
            //뽑은 인덱스가 걸리게 아래 들어가는 point값을 설정해야 함
            binding.luckyWheel.rotateWheelTo(point)
        }
        binding.luckyWheel.setLuckyWheelReachTheTarget {
            //룰렛 다 돌아간 이후 결과 창 떠야 함
            Toast.makeText(this, wheelItems[point - 1].text, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}