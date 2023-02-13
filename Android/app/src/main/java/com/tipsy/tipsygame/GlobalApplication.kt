package com.tipsy.tipsygame

import android.app.Application
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.tipsy.tipsygame.dto.User
import com.kakao.sdk.common.KakaoSdk
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient

private const val TAG = "GlobalApplication"

class GlobalApplication: Application() {

    companion object{
        lateinit var retrofit: Retrofit
        lateinit var gRetrofit: Retrofit
        var stompClient: StompClient? = null
        var user = User("", "test")
        var roomNumber = ""
        var uid = 0L
        var gid = 0
        var sp = SoundPool.Builder()
        .setAudioAttributes(
        AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
        )
        .setMaxStreams(10)
        .build()

        fun connectStomp(){
            val url = "ws://i8d207.p.ssafy.io:8082/ws/chat/websocket"
            if(stompClient == null){
                Log.d(TAG, "connectStomp: ")
                stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
            }
            if(!stompClient!!.isConnected){
                stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
                stompClient?.connect()
            }
        }

        fun reconnectStomp(){
            if(stompClient?.isConnected ?: false){
                stompClient?.disconnect()
                connectStomp()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "c1a2c1e479177783ffcdc37e927517ea")
        retrofit = Retrofit.Builder()
            .baseUrl("http://i8d207.p.ssafy.io:8081")
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
        gRetrofit = Retrofit.Builder()
            .baseUrl("http://i8d207.p.ssafy.io:8082")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

}