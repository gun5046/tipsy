package com.hanjan.hanjangame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.hanjan.hanjangame.databinding.ActivityLoginBinding
import com.hanjan.hanjangame.dto.LoginRequest
import com.hanjan.hanjangame.dto.LoginResult
import com.hanjan.hanjangame.rest.LoginInterface
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { _, error ->
                if (error == null) {
                    login()
                }
            }
        } else {
            binding.kakaoLoginBtn.visibility = View.VISIBLE
        }
        binding.kakaoLoginBtn.setOnClickListener{
//            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
//                if (error != null) {
//                    Log.e(TAG, "로그인 실패", error)
//                }
//                else if (token != null) {
//                    Log.i(TAG, "로그인 성공 ${token.accessToken}")
//                    //서버에서 토큰 받아서 저장하고 레트로핏 헤더 쓰는법 봐야함
//                    //jackson 라이브러리 사용해서 json 사용하는 법 찾기
//                    //ec2에 서버 올리면 GlobalApplication에서 url수정
//                    login()
//                }
//            }
            serverTest()
        }
    }

    fun login(){
        UserApiClient.instance.me { user, error ->
            Log.d(TAG, "onCreate: ${user?.id}")
            val loginRequest = LoginRequest(user?.kakaoAccount?.birthday ?: "", user?.kakaoAccount?.gender.toString(), user?.kakaoAccount?.profile?.profileImageUrl ?: "", user?.id.toString())
            val loginInterface = GlobalApplication.retrofit.create(LoginInterface::class.java)
            loginInterface.login(loginRequest).enqueue(object : Callback<LoginResult>{
                override fun onResponse(
                    call: Call<LoginResult>,
                    response: Response<LoginResult>
                ) {
                    if(response.body() != null){
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "웹에서 회원가입을 해주세요", Toast.LENGTH_SHORT).show()
                        binding.kakaoLoginBtn.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                    //회원가입 웹 redirect
                    Toast.makeText(this@LoginActivity, "인터넷 연결 상태를 확인해주세요", Toast.LENGTH_SHORT).show()
                    binding.kakaoLoginBtn.visibility = View.VISIBLE
                }
            })
        }
    }

    fun serverTest(){
        //현재 에뮬레이터에는 카카오톡이 안깔려 있어서 넘겨야 함
        //실제 폰에서는 서버 연결할 방법이 없으므로 임시 방편임
        val loginRequest = LoginRequest("", "", "", "2627966355")
        val loginInterface = GlobalApplication.retrofit.create(LoginInterface::class.java)
        loginInterface.login(loginRequest).enqueue(object : Callback<LoginResult>{
            override fun onResponse(
                call: Call<LoginResult>,
                response: Response<LoginResult>
            ) {
                if(response.body() != null){
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "웹에서 회원가입을 해주세요", Toast.LENGTH_SHORT).show()
                    binding.kakaoLoginBtn.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                //회원가입 웹 redirect
                Toast.makeText(this@LoginActivity, "인터넷 연결 상태를 확인해주세요", Toast.LENGTH_SHORT).show()
                binding.kakaoLoginBtn.visibility = View.VISIBLE
            }
        })
    }
}