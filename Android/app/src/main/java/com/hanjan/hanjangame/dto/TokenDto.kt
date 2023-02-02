package com.hanjan.hanjangame.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenDto(
    @JsonProperty("accessToken")
    val accessToken: String,
    @JsonProperty("accessTokenExpiresIn")
    val accessTokenExpiresIn: String,
    @JsonProperty("authority")
    val authority: String?,
    @JsonProperty("refreshToken")
    val refreshToken: String
)