package com.epicgames.ue4Network

import android.app.Application
import retrofit2.Retrofit

class AppRetro: Application() {
    var retrofit: Retrofit = Retrofit.Builder()
            //.baseUrl("http://HIDA-4165569.asurite.ad.asu.edu:5000")
            .baseUrl("http://192.168.0.104:5000/")
            .build()
}