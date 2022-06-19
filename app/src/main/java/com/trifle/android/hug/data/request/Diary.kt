package com.trifle.android.hug.data.request

import java.io.Serializable

data class Diary(
    val result : String,
    val message : String,
    val data : List<DiaryResult>,
    val error : List<String>
) : Serializable
data class DiaryResult(
    val index : Int,
    val title : String,
    val content : String,
    val result : TestResult,
    val emoji : Int,
    val createdAt : String
) : Serializable