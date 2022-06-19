package com.trifle.android.hug.presentation.write

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.trifle.android.hug.R
import com.trifle.android.hug.RetrofitClass
import com.trifle.android.hug.data.database.userDb
import com.trifle.android.hug.data.entity.token
import com.trifle.android.hug.data.request.TestResult
import com.trifle.android.hug.data.request.Write
import com.trifle.android.hug.domain.api.API
import com.trifle.android.hug.domain.dto.DiaryDto
import com.trifle.android.hug.domain.dto.Result
import com.trifle.android.hug.presentation.results.ResultActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class WriteActivity : AppCompatActivity() {
    private var db : userDb? = null
    private var rtf : Retrofit? = null
    private var tkList : List<token>? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        val backButton = findViewById<AppCompatButton>(R.id.btnBack)
        val submitButton = findViewById<AppCompatButton>(R.id.btnSubmit)
        val contentEditText = findViewById<EditText>(R.id.etContent)
        val titleEditText = findViewById<EditText>(R.id.etTitle)

        rtf = RetrofitClass().getRetrofitInstance()
        db = userDb.getInstance(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            tkList = db?.tokenDao()?.getAll()
        }

        //addTextListener -> 나중에 알아보기

        backButton.setOnClickListener {
            finish()
        }
        submitButton.setOnClickListener {
            val alt_bld = AlertDialog.Builder(this)
            if(contentEditText.text.isNotEmpty() && titleEditText.text.isNotEmpty()){
                callRequestWrite(contentEditText.text.toString(), titleEditText.text.toString())
            }else { // 제목, 내용중 빈칸이 있을시에 alert => 안됨.
                alt_bld.setTitle("제목과 내용을 모두 입력해주세요.")
                    .setIcon(R.mipmap.ic_main_round)
                    .setPositiveButton("확인",
                        DialogInterface.OnClickListener { dialog, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> dialog.cancel()
                            }
                            dialog.cancel()
                        })
                alt_bld.show()
            }
        }
    }
    private fun callRequestWrite(content : String, title : String){
        val result = Result(0f,0f,0f,0f,0f,0f,0f,0,"","")
        val dto = DiaryDto(0,title,content, result,0,"")
        val api = rtf?.create(API::class.java)
        Log.d("token : ",tkList.toString())
        var strToken = "Bearer ${tkList?.get(0)?.tk}"
        var strEmail = "${tkList?.get(0)?.email}"
        Log.d("token : ",strToken)
        var callAPI = api?.requestWrite(token = strToken,strEmail,dto)

        callAPI?.enqueue(object : Callback<Write>{
            override fun onResponse(call: Call<Write>, response: Response<Write>) {
                if (response.isSuccessful) {
                    Log.d("Write Success", response.code().toString())
                    Log.d("result : ", response.body().toString())
                    val quote = response.body()?.data?.content
                    val speaker = response.body()?.data?.speaker
                    println("quote : $quote\nspeaker : $speaker")
                    updateUI(response.body()!!.data)
                } else{
                    Log.d("Write : Code 400 Error", response.toString())
                }
            }
            override fun onFailure(call: Call<Write>, t: Throwable) {
                Log.d("Write : Code 500 Error", t.toString())
            }
        })

    }
    private fun updateUI(result : TestResult){
        val intent : Intent =  Intent(this, ResultActivity::class.java)
        intent.putExtra("happy",result.happy)
        intent.putExtra("angry",result.angry)
        intent.putExtra("disgust",result.disgust)
        intent.putExtra("fear",result.fear)
        intent.putExtra("sad",result.sad)
        intent.putExtra("neutral",result.neutral)
        intent.putExtra("amazed",result.surprise)
        intent.putExtra("content",result.content)
        intent.putExtra("speaker",result.speaker)
        startActivity(intent)
    }
}
