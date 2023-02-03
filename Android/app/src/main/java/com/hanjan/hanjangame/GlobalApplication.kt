package com.hanjan.hanjangame

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient

private const val TAG = "GlobalApplication"

class GlobalApplication: Application() {

    companion object{
        lateinit var retrofit: Retrofit
        var stompClient: StompClient? = null

        fun connectStomp(){
            val url = "ws://10.0.2.2:8081/ws/chat/websocket"
            if(stompClient == null || stompClient!!.isConnected){
                Log.d(TAG, "connectStomp: ")
                stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
                stompClient?.connect()
            }
        }
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