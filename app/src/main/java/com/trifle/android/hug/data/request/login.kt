package com.trifle.android.hug.data.request

data class login (
    val result : String,
    val message : String,
    val data : loginData,
    val error : List<String>
)
data class loginData(
//    val name : String,
//    val result : MainScreenResult,//HashMap<Int,Float>, // index , percentage 감정 결과.
    val token : String
)


data class mainInfo(
    val result : String,
    val message : String,
    val data : MainScreenResult,
    val error : List<String>
)
data class MainScreenResult(
    val result : HashMap<Int,Float>,
    val emoji : Int,
    val quotes : List<SingleQuoteDto>
)
data class SingleQuoteDto(
    val content : String,
    val speaker : String
)


data class logout(
    val result : String,
    val message : String,
    val data : List<String>,
    val error : List<String>
)