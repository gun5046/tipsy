package com.hanjan.hanjangame

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "c1a2c1e479177783ffcdc37e927517ea")
    }
}