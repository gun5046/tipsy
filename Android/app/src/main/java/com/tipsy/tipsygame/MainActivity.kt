package com.tipsy.tipsygame

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.bumptech.glide.Glide
import com.tipsy.tipsygame.adapter.showGameListDialog
import com.tipsy.tipsygame.databinding.ActivityMainBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.kakao.sdk.user.UserApiClient
import com.tipsy.tipsygame.databinding.CommonDialogBinding
import com.tipsy.tipsygame.rest.RoomInterface
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Glide.with(this)
            .load(GlobalApplication.user.img)
            .error(R.drawable.ic_launcher_foreground).into(binding.profileImg)
        binding.profileNickname.text = GlobalApplication.user.nickname
        binding.logoutBtn.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                }
                else {
                    Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }
        val launcher = registerForActivityResult(ScanContract()){
                result -> if(result.contents != null){
                    //방 번호가 존재하지 않으면 조치 필요
            GlobalApplication.roomNumber = result.contents
//            checkRoom(GlobalApplication.roomNumber)
            val intent = Intent(this@MainActivity, GameRoomActivity::class.java)
            startActivity(intent)
            }
        }
        binding.qrTestBtn.setOnClickListener {
            val options = ScanOptions()
            options.setOrientationLocked(false)
            options.setBeepEnabled(false)
            options.setBarcodeImageEnabled(false)
            options.setPrompt("QR 코드를 인식해주세요")
            launcher.launch(options)
        }
        binding.roomNumberEnterBtn.setOnClickListener {
            if(binding.roomNumberText.text.isBlank()){
                Toast.makeText(this, "방 코드를 확인해주세요", Toast.LENGTH_SHORT).show()

            } else {
                GlobalApplication.roomNumber = binding.roomNumberText.text.toString()
//                checkRoom(GlobalApplication.roomNumber)
                binding.roomNumberText.text.clear()
                val intent = Intent(this@MainActivity, GameRoomActivity::class.java)
                startActivity(intent)
            }
        }
        binding.gameListBtn.setOnClickListener {
            showGameListDialog(this)
        }
    }

    fun checkRoom(rid: String){
        val RoomInterface = GlobalApplication.gRetrofit.create(RoomInterface::class.java)
        RoomInterface.checkRoom(GlobalApplication.uid, rid).enqueue(object: Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d(TAG, "onResponse: $response")
                if(response.body() != null){
                    val result = response.body()!!
                    Log.d(TAG, "onResponse: $result")
                    if(result.equals("True")){
                        binding.roomNumberText.text.clear()
                        val intent = Intent(this@MainActivity, GameRoomActivity::class.java)
                        startActivity(intent)
                    } else if(result.equals("WrongRoomId")){
                        showDialog("존재하지 않는 방입니다")
                        //없는 방
                    } else if(result.equals("WrongUser")){
                        showDialog("입장 불가능한 방입니다")
                        //방에 없는 사용자
                    } else if(result.equals("Playing")){
                        showDialog("게임 중인 방입니다")
                        //게임중
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun showDialog(message: String){
        val builder = AlertDialog.Builder(this, R.style.Theme_dialog)
        val binding = CommonDialogBinding.inflate(LayoutInflater.from(this))
        builder.setView(binding.root)
        binding.messageText.text = message
        val dialog = builder.show()
        binding.button.setOnClickListener {
            dialog.dismiss()
        }
    }
}