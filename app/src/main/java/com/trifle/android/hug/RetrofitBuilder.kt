package com.trifle.android.hug

import com.trifle.android.hug.domain.API
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    lateinit var api : API
    init{
        val retrofit = Retrofit.Builder()
            .baseUrl("")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(API::class.java)
    }
}