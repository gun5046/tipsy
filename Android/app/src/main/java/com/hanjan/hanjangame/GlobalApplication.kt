package com.hanjan.hanjangame

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

class GlobalApplication: Application() {

    companion object{
        lateinit var retrofit: Retrofit
    }

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "c1a2c1e479177783ffcdc37e927517ea")
        retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081")
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
    }
}