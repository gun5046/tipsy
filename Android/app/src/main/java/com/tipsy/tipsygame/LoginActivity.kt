package com.tipsy.tipsygame

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.tipsy.tipsygame.databinding.ActivityLoginBinding
import com.tipsy.tipsygame.dto.LoginRequest
import com.tipsy.tipsygame.dto.LoginResult
import com.tipsy.tipsygame.rest.LoginInterface
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import com.tipsy.tipsygame.databinding.CommonDialogBinding
import com.tipsy.tipsygame.dto.User
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
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e(TAG, "로그인 실패", error)
                }
                else if (token != null) {
                    Log.i(TAG, "로그인 성공 ${token.accessToken}")
                    login()
                }
            }
//            val intent = Intent(this@LoginActivity, MainActivity::class.java)
//            startActivity(intent)
//            finish()
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
                        response.body()!!.apply {
                            GlobalApplication.user = User(userVo!!.image, userVo!!.nickname)
                            GlobalApplication.uid = userVo!!.uid
                        }
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val builder = AlertDialog.Builder(this@LoginActivity)
                        val dialogBinding = CommonDialogBinding.inflate(LayoutInflater.from(this@LoginActivity))
                        builder.setView(dialogBinding.root)
                        dialogBinding.messageText.text = "웹에서 회원가입을 해주세요"
                        val dialog = builder.show()
                        dialogBinding.button.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://i8d207.p.ssafy.io/"))
                            startActivity(intent)
                            dialog.dismiss()
                        }
                        binding.kakaoLoginBtn.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                    //회원가입 웹 redirect
                    Log.d(TAG, "onFailure: ${t.message}")
                    Toast.makeText(this@LoginActivity, "인터넷 연결 상태를 확인해주세요", Toast.LENGTH_SHORT).show()
                    binding.kakaoLoginBtn.visibility = View.VISIBLE
                }
            })
        }
    }
}