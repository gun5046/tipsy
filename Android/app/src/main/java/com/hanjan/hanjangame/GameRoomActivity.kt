package com.hanjan.hanjangame

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanjan.hanjangame.adapter.UserListAdapter
import com.hanjan.hanjangame.adapter.showGameListDialog
import com.hanjan.hanjangame.databinding.ActivityGameRoomBinding
import com.hanjan.hanjangame.databinding.GameExitDialogBinding
import com.hanjan.hanjangame.dto.UserWithData

class GameRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameRoomBinding
    val userList = listOf(
        UserWithData("", "사용자 1", true, false),
        UserWithData("", "사용자 2", false, false),
        UserWithData("", "사용자 3", false, true)
    )
    private var host = false
    private var ready = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val roomNumber = intent.getStringExtra("roomNumber")
        binding.gameStartBtn.setOnClickListener {
            startActivity(Intent(this, GameListActivity::class.java))
        }
        binding.backButton.setOnClickListener {
            showExitDialog()
        }
        binding.gameRoomListBtn.setOnClickListener {
            showGameListDialog(this)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = UserListAdapter(userList)
        binding.recyclerView.suppressLayout(true)
        host = true // 테스트용으로 host를 임시 지정
        if(host){
            binding.gameStartBtn.text = "시작"
        } else {
            binding.gameStartBtn.text = "준비"
        }
        binding.gameStartBtn.setOnClickListener {
            //host인 경우
            if(host){
                //모든 유저가 준비 상태인지 확인 필요
                //시작하면 게임 시작한다는 메시지 서버에 보낸 후 방의 모든 사용자가 받아야 함
                //사용자는 게임 시작한다는 메시지를 받아야 다음 액티비티로 넘어가야 함
                //액티비티가 넘어갈 때 현재 사용자가 Host인지 유무가 Boolean으로 넘어가야 함
                val intent = Intent(this, GameListActivity::class.java)
                intent.putExtra("host", host)
                startActivity(intent)
            } else {
                //아닐 경우 준비하는 로직 필요
                //준비 버튼을 누를 경우 준비했다는 메시지 서버로 보내야 함
                //준비 상태일 경우 준비 해제 버튼이 되어야 함
                //준비 해제를 할 경우 준비 해제 메시지 보내야 함
                if(ready){//준비 해제
                    ready = false
                    binding.gameStartBtn.text = "준비"
                } else {//준비
                    ready = true
                    binding.gameStartBtn.text = "준비 해제"
                }
            }
        }
    }

    override fun onBackPressed() {
        showExitDialog()
    }

    fun showExitDialog(){
        val builder = AlertDialog.Builder(this)
        val dialogBinding = GameExitDialogBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)
        val dialog = builder.show()
        dialogBinding.dialogExitBtn.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialogBinding.dialogCancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }
}