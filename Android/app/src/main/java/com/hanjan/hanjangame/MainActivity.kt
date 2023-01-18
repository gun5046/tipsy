package com.hanjan.hanjangame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
            }
        }
        UserApiClient.instance.scopes { scopeInfo, error->
            if (error != null) {
                Log.e(TAG, "동의 정보 확인 실패", error)
            }else if (scopeInfo != null) {
                Log.i(TAG, "동의 정보 확인 성공\n 현재 가지고 있는 동의 항목 $scopeInfo")
            }
        }
        binding.logout.setOnClickListener {
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Log.e(TAG, "연결 끊기 실패", error)
                }
                else {
                    Log.i(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                }
            }
            finish()
        }
    }
}