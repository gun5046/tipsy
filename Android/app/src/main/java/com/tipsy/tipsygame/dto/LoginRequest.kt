package com.tipsy.tipsygame.dto

data class LoginRequest(
    val birth: String,
    val gender: String,
    val image: String,
    val kakao_id: String
)