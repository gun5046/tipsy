package com.tipsy.tipsygame.rest

import com.tipsy.tipsygame.dto.LoginRequest
import com.tipsy.tipsygame.dto.LoginResult
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginInterface {
    @POST("/user/check")
    fun login(@Body loginRequest: LoginRequest) : Call<LoginResult>
}