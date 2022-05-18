package com.trifle.android.hug.domain

import com.trifle.android.hug.domain.login.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

public interface API {
    @POST("android")
    fun getLoginReponse(@Body user : User) : Call<String>
}