package com.trifle.android.hug.domain.dto


data class DiaryDto(
    val index:  Int,
    val title: String,
    val content: String,
    val result: Result,
    val emoji: Int,
    val createdAt: String
)
data class Result(
    val happy : Float,
    val angry : Float,
    val disgust : Float,
    val fear : Float,
    val sad : Float,
    val neutral : Float,
    val surprise : Float,
    val index : Int,
    val content : String,
    val speaker : String
)