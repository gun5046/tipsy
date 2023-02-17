package com.tipsy.tipsygame.rest

import com.tipsy.tipsygame.dto.LoginRequest
import com.tipsy.tipsygame.dto.LoginResult
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

interface RoomInterface {
    @GET("/game/room")
    fun checkRoom(@Query("uid") uid: Long, @Query("rid") rid: String) : Call<String>
}