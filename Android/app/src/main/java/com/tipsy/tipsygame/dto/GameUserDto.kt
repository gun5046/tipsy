package com.tipsy.tipsygame.dto

class GameUserDto(img: String, nickname: String, val host: Boolean, var ready: Boolean) : User(img, nickname) {//게임에 필요한 host, 준비 유무 포함 데이터
}