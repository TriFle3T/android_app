package com.trifle.android.hug.domain.api

import com.trifle.android.hug.data.request.*
import com.trifle.android.hug.domain.dto.DiaryDto
import com.trifle.android.hug.domain.dto.SignInRequestDto
import com.trifle.android.hug.domain.dto.SignOutRequestDto
import retrofit2.Call
import retrofit2.http.*

interface API {

//    @FormUrlEncoded
    @POST("/api/v1/sign-in") // login API
    fun requestLogin( //아래 수정 필요.
        @Body signInDto : SignInRequestDto
    ) : Call<login>

    @POST("/api/v1/user/{email}/diary") // write submit API // 안됨 : api url 바뀜
    fun requestWrite(
        @Header("Authorization") token : String,
        @Path("email") email : String,
        @Body diaryDto : DiaryDto
    ) : Call<Write>

    @POST("/api/v1/user/{email}/sign-out")
    fun requestLogout(
        @Header("Authorization") token : String,
        @Path("email") email : String,
        @Body signOutRequestDto: SignOutRequestDto
    ) : Call<logout>

    @GET("/api/v1/user/{email}")
    fun requestMainInfo(
        @Header("Authorization") token : String,
        @Path("email") email : String
    ) : Call<mainInfo>

    @GET("/api/v1/user/{email}/diary")
    fun requestDiary(
        @Header("Authorization") token : String,
        @Path("email") email : String
    ) : Call<Diary>

    // delete diary 구현해야함.
    @DELETE("/api/v1/user/{email}/diary/{index}")
    fun requestDelete(
        @Header("Authorization") token : String,
        @Path("email") email : String,
        @Path("index") index : Int
    ) : Call<Diary>
}