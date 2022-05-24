package com.trifle.android.hug.domain.api

import com.trifle.android.hug.data.request.User
import com.trifle.android.hug.domain.dto.SignInRequestDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface API {

//    @FormUrlEncoded
    @POST("/api/v1/sign-in") // login API URL
    fun requestLogin( //아래 수정 필요.
        @Body signInDto : SignInRequestDto
    ) : Call<User>

}