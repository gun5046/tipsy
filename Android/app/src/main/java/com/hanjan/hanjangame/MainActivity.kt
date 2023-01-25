package com.hanjan.hanjangame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.hanjan.hanjangame.databinding.ActivityMainBinding
import com.kakao.sdk.user.UserApiClient

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            }
            else if (user != null) {
                Log.i(TAG, "사용자 정보 요청 성공" +
                        "\n회원번호: ${user.id}" +
                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                        "\n프로필사진: ${user.kakaoAccount?.profile?.profileImageUrl}" +
                        "\n이메일: ${user.kakaoAccount?.email}" +
                        "\n성별: ${user.kakaoAccount?.gender}" +
                        "\n연령대: ${user.kakaoAccount?.ageRange}" +
                        "\n생일: ${user.kakaoAccount?.birthday}")
                Glide.with(this)
                    .load(user.kakaoAccount?.profile?.profileImageUrl)
                    .error(R.drawable.ic_launcher_foreground).into(binding.profileImg)
            }
        }
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
    }
}