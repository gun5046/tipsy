package com.tipsy.tipsygame.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.tipsy.tipsygame.GlobalApplication
import org.json.JSONObject

class ForceExitService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val data = JSONObject()
        data.put("type", "ForceExit")
        val userJson = JSONObject()
        userJson.put("img", GlobalApplication.user.img)
        userJson.put("nickname", GlobalApplication.user.nickname)
        userJson.put("host", false)
        userJson.put("ready", false)
        data.put("gameUserDto", userJson)
        data.put("gid", GlobalApplication.gid)
        GlobalApplication.stompClient!!.send("/game/force-exit/${GlobalApplication.roomNumber}", data.toString())?.subscribe()
        Log.d("ForceExit", "onTaskRemoved: ${GlobalApplication.stompClient?.isConnected}")
        super.onTaskRemoved(rootIntent)
    }
}