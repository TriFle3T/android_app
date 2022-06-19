package com.trifle.android.hug.presentation.diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.trifle.android.hug.*
import com.trifle.android.hug.data.database.userDb
import com.trifle.android.hug.data.entity.token
import com.trifle.android.hug.data.request.Diary
import com.trifle.android.hug.data.request.DiaryResult
import com.trifle.android.hug.data.request.mainInfo
import com.trifle.android.hug.domain.api.API
import com.trifle.android.hug.presentation.logout.LogoutActivity
import com.trifle.android.hug.presentation.results.ResultActivity
import com.trifle.android.hug.presentation.write.WriteActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class DiaryActivity : AppCompatActivity(){
    private var db : userDb? = null
    private var rtf : Retrofit? = null
    private var tkList : List<token>? = null

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: DiaryAdapter
    private var listener : TabLayout.OnTabSelectedListener? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)
        val userBotton = findViewById<AppCompatButton>(R.id.barUser)
        val homeButton = findViewById<AppCompatButton>(R.id.btnHome)
        val writeButton = findViewById<AppCompatButton>(R.id.btnWrite)
        rtf = RetrofitClass().getRetrofitInstance()
        db = userDb.getInstance(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            tkList = db?.tokenDao()?.getAll()
            callRequestDiary()
        }

        userBotton.setOnClickListener {
            val intent : Intent =  Intent(this, LogoutActivity::class.java)
            intent.putExtra("emoji",getIntent().getIntExtra("emoji",-1))
            startActivity(intent)
            finish()
        }
        homeButton.setOnClickListener {
            val intent : Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        writeButton.setOnClickListener {
            val intent : Intent = Intent(this, WriteActivity::class.java)
            startActivity(intent)
        }

        setEmotion()


    }
    private fun callRequestDiary(){
        val api = rtf?.create(API::class.java)
        val strToken = "Bearer ${tkList?.get(0)?.tk}"
        val strEmail = "${tkList?.get(0)?.email}"
        val callAPI = api?.requestDiary(token = strToken,strEmail)
        callAPI?.enqueue(object : Callback<Diary>{
            override fun onResponse(call: Call<Diary>, response: Response<Diary>) {
                if (response.isSuccessful) {
                    Log.d("Diary success", response.code().toString())
                    val result = response.body()?.data
                    Log.d("In Diary API: ",response.body().toString())
                    if (result != null) {
                        initUI(result)
                    }
                    else{
                        Log.d("Diary : Empty!", response.body()?.data.toString())
                    }
                } else{
                    Log.d("Diary : Code 400 Error", response.toString())
                }
            }
            override fun onFailure(call: Call<Diary>, t: Throwable) {
                Log.d("Diary : Code 500 Error", t.toString())
            }
        })

    }
    private fun callRequestDelete(index : Int){
        val api = rtf?.create(API::class.java)
        val strToken = "Bearer ${tkList?.get(0)?.tk}"
        val strEmail = "${tkList?.get(0)?.email}"
        val callAPI = api?.requestDelete(token = strToken,strEmail,index)
        callAPI?.enqueue(object : Callback<Diary>{
            override fun onResponse(call: Call<Diary>, response: Response<Diary>) {
                if (response.isSuccessful) {
                    Log.d("Delete success", response.code().toString())
                    Log.d("In Delete API: ",response.body().toString())
                    callRequestDiary()

                } else{
                    Log.d("Delete : Code 400 Error", response.toString())
                }

            }
            override fun onFailure(call: Call<Diary>, t: Throwable) {
                Log.d("Delete : Code 500 Error", t.toString())
            }
        })

    }
    private fun initUI(items : List<DiaryResult>){
        val spaceDecoration = DiaryDecoration(5)
        recyclerView = findViewById(R.id.recyclerView)
        adapter = DiaryAdapter(items)
        // connect adapter to recycler view
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        recyclerView.addItemDecoration(spaceDecoration)
        adapter.setOnItemClickListener(object : OnDiaryClickListener{
            override fun onItemClick(holder: DiaryAdapter.ViewHolder, view: View, position: Int) {
                val item = adapter.getItem(position)
                Log.d("Diary item : ", item.toString())
                Intent(this@DiaryActivity,
                    DetailActivity::class.java).apply {
                    putExtra("metaData",item)
                    putExtra("emoji",item?.emoji)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run{ startActivity(this) }
            }

            override fun onDeleteClick(holder: DiaryAdapter.ViewHolder, view: View, index: Int, position : Int) {
                Log.d("In delete button clikced listener : ", index.toString())
                adapter.notifyItemRemoved(position) //recycler view에 삭제됨을 알림
                callRequestDelete(index) //API 호출
            }
        })

    }
    private fun setEmotion(){
        val imgEmoji = findViewById<ImageView>(R.id.barEmoji)
        val emo = intent.getIntExtra("emoji",-1)
        when(emo){
            0 -> imgEmoji?.setImageResource(R.drawable.ic_happy)
            1 -> imgEmoji?.setImageResource(R.drawable.ic_angry)
            2 -> imgEmoji?.setImageResource(R.drawable.ic_disgust)
            3 -> imgEmoji?.setImageResource(R.drawable.ic_fear)
            4 -> imgEmoji?.setImageResource(R.drawable.ic_neutral)
            5 -> imgEmoji?.setImageResource(R.drawable.ic_sad)
            6 -> imgEmoji?.setImageResource(R.drawable.ic_amazed)
            7 -> imgEmoji?.setImageResource(R.drawable.ic_guitar)
        }
    }
}