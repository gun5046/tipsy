package com.hanjan.hanjangame.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginResult(
    @JsonProperty("tokenDto")
    val tokenDto: TokenDto?,
    @JsonProperty("userCheck")
    val userCheck: Boolean,
    @JsonProperty("userVo")
    val userVo: UserVo?
)