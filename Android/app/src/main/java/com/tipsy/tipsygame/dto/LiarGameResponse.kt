package com.tipsy.tipsygame.dto

data class LiarGameResponse (
    val category: String,
    val word: String,
    val liar: String,
    val gameUserList: List<GameUserDto>
    )