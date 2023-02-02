package com.hanjan.hanjangame.rest

import com.hanjan.hanjangame.dto.LoginRequest
import com.hanjan.hanjangame.dto.LoginResult
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginInterface {
    @POST("/user/check")
    fun login(@Body loginRequest: LoginRequest) : Call<LoginResult>
}