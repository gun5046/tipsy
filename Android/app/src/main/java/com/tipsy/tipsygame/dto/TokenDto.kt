package com.tipsy.tipsygame.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenDto(
    @JsonProperty("accessToken")
    val accessToken: String,
    @JsonProperty("refreshToken")
    val refreshToken: String
)