package com.tipsy.tipsygame

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.tipsy.tipsygame.adapter.UserListAdapter
import com.tipsy.tipsygame.adapter.showGameListDialog
import com.tipsy.tipsygame.databinding.ActivityGameRoomBinding
import com.tipsy.tipsygame.databinding.GameExitDialogBinding
import com.tipsy.tipsygame.dto.GameUserDto
import org.json.JSONObject

private const val TAG = "GameRoomActivity"

class GameRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameRoomBinding
    var userList = listOf<GameUserDto>()
    private var roomNumber = GlobalApplication.roomNumber
    private val img = GlobalApplication.user.img
    private val nickname = GlobalApplication.user.nickname
    private var host = false
    private var ready = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "onCreate: $roomNumber")
        GlobalApplication.connectStomp()
        GlobalApplication.gid = 0
        subscribeStomp()
        sendMessage("Enter")
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
        renameButton()
        binding.gameStartBtn.setOnClickListener {
            //host인 경우
            if(host){
                //모든 유저가 준비 상태인지 확인 필요
                //시작하면 게임 시작한다는 메시지 서버에 보낸 후 방의 모든 사용자가 받아야 함
                //사용자는 게임 시작한다는 메시지를 받아야 다음 액티비티로 넘어가야 함
                //액티비티가 넘어갈 때 현재 사용자가 Host인지 유무가 Boolean으로 넘어가야 함
                if(userList.size == 1){
                    Toast.makeText(this, "혼자서는 게임을 시작할 수 없습니다.", Toast.LENGTH_SHORT).show()
                } else
                    if(checkReady()){
                    ready = true
                    sendMessage("Start")
                } else {
                    //모든 사용자가 준비 해야한다는 메시지 출력
                    Toast.makeText(this, "모든 사용자가 준비하지 않았습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                //아닐 경우 준비하는 로직 필요
                //준비 버튼을 누를 경우 준비했다는 메시지 서버로 보내야 함
                //준비 상태일 경우 준비 해제 버튼이 되어야 함
                //준비 해제를 할 경우 준비 해제 메시지 보내야 함
                if(ready){//준비 해제
                    ready = false
                    sendMessage("Ready")
                } else {//준비
                    ready = true
                    sendMessage("Ready")
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        GlobalApplication.reconnectStomp()
        GlobalApplication.gid = 0
        ready = false
        subscribeStomp()
        sendMessage("Ready")
    }

    override fun onBackPressed() {
        showExitDialog()
    }

    fun showExitDialog(){
        val builder = AlertDialog.Builder(this, R.style.Theme_dialog)
        val dialogBinding = GameExitDialogBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)
        val dialog = builder.show()
        dialogBinding.dialogExitBtn.setOnClickListener {
            dialog.dismiss()
            sendMessage("Exit")
            GlobalApplication.stompClient?.disconnect()
            finish()
        }
        dialogBinding.dialogCancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun sendMessage(type: String){
        val data = JSONObject()
        data.put("type", type)
        val userJson = JSONObject()
        userJson.put("img", img)
        userJson.put("nickname", nickname)
        userJson.put("host", host)
        userJson.put("ready", ready)
        data.put("gameUserDto", userJson)
        GlobalApplication.stompClient!!.send("/game/room/${roomNumber}", data.toString())?.subscribe()
    }

    fun renameButton(){
        if(host){
            binding.gameStartBtn.text = "시작"
        } else {
            if(ready){
                binding.gameStartBtn.text = "준비 해제"
            } else {
                binding.gameStartBtn.text = "준비"
            }
        }
    }

    fun startGame(){
        val intent = Intent(this, GameListActivity::class.java)
        intent.putExtra("host", host)
        startActivity(intent)
    }

    fun checkReady() : Boolean{
        userList.forEach {
            if(!it.host && !it.ready){
                return false
            }
        }
        return true
    }

    fun subscribeStomp(){
        GlobalApplication.stompClient?.topic("/sub/room/${roomNumber}")?.subscribe{
            Log.d(TAG, "onCreate: ${it.payload}")
            val list = jacksonObjectMapper().readValue<List<GameUserDto>>(it.payload)
            userList = list
            runOnUiThread {
                binding.recyclerView.adapter = UserListAdapter(list)
            }
            list.forEach {
                if(it.nickname.equals(nickname)){
                    host = it.host
                    ready = it.ready
                }
                if(it.host && it.ready){
                    startGame()
                }
            }
            runOnUiThread {
                renameButton()
            }
        }
    }
}