package com.hanjan.hanjangame.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserVo(
    @JsonProperty("birth")
    val birth: String,
    @JsonProperty("gender")
    val gender: String,
    @JsonProperty("image")
    val image: String,
    @JsonProperty("kakao_id")
    val kakao_id: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("interest")
    val interest: String,
    @JsonProperty("nickname")
    val nickname: String,
    @JsonProperty("reportcnt")
    val reportcnt: Int,
    @JsonProperty("uid")
    val uid: Int
)