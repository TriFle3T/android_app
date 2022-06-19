package com.trifle.android.hug.data.request

import java.io.Serializable

data class Write(
    val result : String,
    val message : String,
    val data : TestResult,
    val error : List<String>
)
data class TestResult(
    val happy : Float,
    val angry : Float,
    val disgust : Float,
    val fear : Float,
    val sad : Float,
    val neutral : Float,
    val surprise : Float,
    val resultIndex : Int,
    val quoteIndex : Int,
    val content : String,
    val speaker : String
) : Serializable
