package com.trifle.android.hug

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.viewpager2.widget.ViewPager2
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.trifle.android.hug.data.database.userDb
import com.trifle.android.hug.data.entity.token
import com.trifle.android.hug.data.request.mainInfo
import com.trifle.android.hug.domain.api.API
import com.trifle.android.hug.presentation.color
import com.trifle.android.hug.presentation.diary.DiaryActivity
import com.trifle.android.hug.presentation.login.LoginActivity
import com.trifle.android.hug.presentation.logout.LogoutActivity
import com.trifle.android.hug.presentation.write.WriteActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class MainActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var rtf : Retrofit? = null
    private var db : userDb? = null
    private var tkList : List<token>? = null
    private var emo : Int? = null
    private var quotes : ArrayList<String>? = arrayListOf()
    private var adpter : ViewPagerAdapter? = null
    private var imgEmoji : ImageView? = null

    private var ptgData = arrayListOf<Float>()
    private var idxData = arrayListOf<Int>()
    private var emoList : HashMap<Int,Float>? = null

    private var chart : PieChart? = null
    private val cc = color()
    private fun initFirebase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("get Message success",task.toString())
            }
            val token = task.result
            Log.d("MessageToken : ",token)
        }
    }
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFirebase()
        imgEmoji = findViewById(R.id.barEmoji)
        val leftButton = findViewById<AppCompatButton>(R.id.btnLeft)
        val rightButton = findViewById<AppCompatButton>(R.id.btnRight)

        rtf = RetrofitClass().getRetrofitInstance()
        db = userDb.getInstance(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            tkList = db?.tokenDao()?.getAll()
            Log.d("token : ",tkList.toString())
            if(tkList != null) {
                if(tkList!!.size != 0)
                    callGetMainInfo(leftButton, rightButton)
            }
        }


        val userBotton = findViewById<AppCompatButton>(R.id.barUser)
        val diaryButton = findViewById<AppCompatButton>(R.id.btnDown)
        val writeButton = findViewById<AppCompatButton>(R.id.btnWrite)

        userBotton.setOnClickListener {
            val intent : Intent =  Intent(this, LogoutActivity::class.java)
            intent.putExtra("emoji",emo)
            startActivity(intent)
            finish()
        }
        writeButton.setOnClickListener {
            val intent : Intent = Intent(this, WriteActivity::class.java)
            startActivity(intent)
//            finish()
        }
        diaryButton.setOnClickListener {
            val intent : Intent = Intent(this, DiaryActivity::class.java)
            intent.putExtra("emoji",emo)
            startActivity(intent)
            finish()
        }
    }
    private fun addDataSet() {
        var yEntrys = arrayListOf<PieEntry>()
        var xEntrys = arrayListOf<Int>()

        for(idx in 0..(ptgData.size-1)){
            yEntrys.add(PieEntry(ptgData[idx], idx))
        }
        for(idx in 0..(idxData.size-1)){
            xEntrys.add(idxData[idx])
        }
        var pieDataSet = PieDataSet(yEntrys, "")
        pieDataSet.sliceSpace = 2f
        pieDataSet.valueTextSize = 0f

        var colors = arrayListOf<Int>()
        Log.d("colors",emoList?.keys.toString())
        emoList?.keys?.forEach { it ->
            when(it){
                0 -> colors.add(cc.happy)
                1 -> colors.add(cc.angry)
                2 -> colors.add(cc.disgust)
                3 -> colors.add(cc.fear)
                4 -> colors.add(cc.neutral)
                5 -> colors.add(cc.sad)
                6 -> colors.add(cc.amazed)
                7 -> colors.add(cc.guitar)
            }
        }
        pieDataSet.setColors(colors)

        var legend = chart?.legend
        legend?.isEnabled = false

        var pieData = PieData(pieDataSet)
        chart?.data = pieData
        chart?.invalidate()

    }

    public override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun callGetMainInfo(lbtn :AppCompatButton, rbtn : AppCompatButton) {
        val api = rtf?.create(API::class.java)
        val strToken = "Bearer ${tkList?.get(0)?.tk}"
        val strEmail = "${tkList?.get(0)?.email}"
        val callAPI = api?.requestMainInfo(token = strToken,strEmail)

        Log.d("Main : email : ",strEmail)

        callAPI?.enqueue(object : Callback<mainInfo>{
            override fun onResponse(call: Call<mainInfo>, response: Response<mainInfo>) {
                if (response.isSuccessful) {
                    Log.d("Main Success", response.code().toString())
                    val result = response.body()?.data
                    var qt = result?.quotes
                    emo = result?.emoji
                    emoList = result?.result
                    Log.d("In Main API: ",response.body().toString())
                    if (qt != null) {
                        for(idx in 0..(qt.size-1)){
                            if(qt[idx].speaker != null)
                                quotes?.add("${qt[idx].content}\n-${qt[idx].speaker}-")
                            else
                                quotes?.add("${qt[idx].content}")
                        }
                    }else{
                        quotes?.add("TriFle")
                    }
                    getMainScreen(lbtn,rbtn)
                } else{
                    Log.d("Main : Code 400 Error", response.toString())
                }
            }
            override fun onFailure(call: Call<mainInfo>, t: Throwable) {
                Log.d("Main : Code 500 Error", t.toString())
            }
        })
    }
    private fun getMainScreen(leftButton : AppCompatButton, rightButton : AppCompatButton){
        adpter = quotes?.let { ViewPagerAdapter(it) }
        val vpager = findViewById<ViewPager2>(R.id.tvContent)
        vpager.bringToFront()
        vpager.adapter = adpter

        vpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int){
                val tv = findViewById<TextView>(R.id.list_item)
                super.onPageSelected(position)
            }
        })
        // Bar Emoji
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
        // Chart Emoji
        val emo1 = findViewById<ImageView>(R.id.imgEmo1) ; val tv1 = findViewById<TextView>(R.id.tvEmo1)
        val emo2 = findViewById<ImageView>(R.id.imgEmo2) ; val tv2 = findViewById<TextView>(R.id.tvEmo2)
        val emo3 = findViewById<ImageView>(R.id.imgEmo3) ; val tv3 = findViewById<TextView>(R.id.tvEmo3)
        val emo4 = findViewById<ImageView>(R.id.imgEmo4) ; val tv4 = findViewById<TextView>(R.id.tvEmo4)
        val emo5 = findViewById<ImageView>(R.id.imgEmo5) ; val tv5 = findViewById<TextView>(R.id.tvEmo5)
        var idx = 1
        emoList?.forEach { (key,value) ->
            idxData.add(key)
            ptgData.add(value)
            when(idx){
                1 -> whichEmotion(key,value,emo1,tv1)
                2 -> whichEmotion(key,value,emo2,tv2)
                3 -> whichEmotion(key,value,emo3,tv3)
                4 -> whichEmotion(key,value,emo4,tv4)
                5 -> whichEmotion(key,value,emo5,tv5)
            }
            idx += 1
        }
        //Pie Chart
        chart = findViewById<PieChart>(R.id.pcChart)
        chart?.apply {
            this.isRotationEnabled = true
            this.holeRadius = 70f
            this.setTransparentCircleAlpha(0)
            this.centerText = "HUG"
            this.setCenterTextSize(25f)
            setCenterTextColor(Color.WHITE)
            setHoleColor(Color.GRAY)
            this.setDrawEntryLabels(false)
            this.description.isEnabled = false
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }
        addDataSet()
        // Quote
        var quoteNum : Int? = 0
        if(quotes?.size == 1){
            quoteNum = 1
        } else {
            quoteNum = adpter?.quoteNum
        }
        leftButton.setOnClickListener {
            val cur = vpager.currentItem
            if(cur == 0){
                if (quoteNum != null) {
                    vpager.setCurrentItem(quoteNum-1,true)
                }
            }else{
                vpager.setCurrentItem(cur-1,true)
            }
        }
        rightButton.setOnClickListener {
            val cur = vpager.currentItem
            if (quoteNum != null) {
                if(cur == (quoteNum-1)){
                    vpager.setCurrentItem(0,true)
                }else{
                    vpager.setCurrentItem(cur+1,true)
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun whichEmotion(emotion : Int, percentage : Float, imgView : ImageView, textView: TextView){
        val data = String.format("%.1f",percentage)
        when(emotion){
            0 -> {
                imgView.setImageResource(R.drawable.ic_happy)
                textView.setText("${data}%")
                textView.setTextColor(cc.happy)
            }
            1 -> {
                imgView.setImageResource(R.drawable.ic_angry)
                textView.setText("${data}%")
                textView.setTextColor(cc.angry)
            }
            2 -> {
                imgView.setImageResource(R.drawable.ic_disgust)
                textView.setText("${data}%")
                textView.setTextColor(cc.disgust)
            }
            3 -> {
                imgView.setImageResource(R.drawable.ic_fear)
                textView.setText("${data}%")
                textView.setTextColor(cc.fear)
            }
            4 -> {
                imgView.setImageResource(R.drawable.ic_neutral)
                textView.setText("${data}%")
                textView.setTextColor(cc.neutral)
            }
            5 -> {
                imgView.setImageResource(R.drawable.ic_sad)
                textView.setText("${data}%")
                textView.setTextColor(cc.sad)
            }
            6 -> {
                imgView.setImageResource(R.drawable.ic_amazed)
                textView.setText("${data}%")
                textView.setTextColor(cc.amazed)
            }
            7 -> {
                imgView.setImageResource(R.drawable.ic_guitar)
                textView.setText("${data}%")
                textView.setTextColor(cc.guitar)
            }

        }
    }
}