package com.trifle.android.hug.presentation.diary

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.trifle.android.hug.MainActivity
import com.trifle.android.hug.R
import com.trifle.android.hug.data.request.DiaryResult
import com.trifle.android.hug.presentation.color
import com.trifle.android.hug.presentation.logout.LogoutActivity

class DetailActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diary_detail)
        val homeButton = findViewById<AppCompatButton>(R.id.btnHome)
        val backButton = findViewById<AppCompatButton>(R.id.btnBack)
        val content = findViewById<TextView>(R.id.tvContent)
        val bcEmotion = findViewById<HorizontalBarChart>(R.id.bcEmotion)
        val meta = intent.getSerializableExtra("metaData") as DiaryResult
        content.text = meta.content // 일기 내용 설정

        backButton.setOnClickListener {
            finish()
        }
        homeButton.setOnClickListener {
            val intent : Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        val x = bcEmotion.xAxis
        val y = bcEmotion.axisLeft
        val z = bcEmotion.axisRight
        bcEmotion?.apply{
            description.isEnabled = false
            setTouchEnabled(true)
            legend.isEnabled = false
//            setExtraOffsets(10f,0f,40f,0f)
        }
        x.setDrawAxisLine(false)
        x.isEnabled = false
        x.granularity = 1f
        x.textColor = R.color.box
        x.textSize = 0f
        x.gridLineWidth = 20f
        x.gridColor = R.color.background_color

        y.setDrawGridLines(false)
        y.setDrawAxisLine(false)
        y.setDrawLabels(false)
        z.setDrawLabels(false)
        z.setDrawGridLines(false)
        z.setDrawAxisLine(false)

        bcEmotion.data = setData(meta)
        bcEmotion.invalidate()

        val img = findViewById<ImageView>(R.id.img) ; val tv = findViewById<TextView>(R.id.tv)
        val img1 = findViewById<ImageView>(R.id.img1) ; val tv1 = findViewById<TextView>(R.id.tv1)
        val img2 = findViewById<ImageView>(R.id.img2) ; val tv2 = findViewById<TextView>(R.id.tv2)
        val img3 = findViewById<ImageView>(R.id.img3) ; val tv3 = findViewById<TextView>(R.id.tv3)
        val img4 = findViewById<ImageView>(R.id.img4) ; val tv4 = findViewById<TextView>(R.id.tv4)
        val img5 = findViewById<ImageView>(R.id.img5) ; val tv5 = findViewById<TextView>(R.id.tv5)
        val img6 = findViewById<ImageView>(R.id.img6) ; val tv6 = findViewById<TextView>(R.id.tv6)
        setEmotion(6,meta.result.surprise,img,tv)
        setEmotion(5,meta.result.sad,img1,tv1)
        setEmotion(4,meta.result.neutral,img2,tv2)
        setEmotion(3,meta.result.fear,img3,tv3)
        setEmotion(2,meta.result.disgust,img4,tv4)
        setEmotion(1,meta.result.angry,img5,tv5)
        setEmotion(0,meta.result.happy,img6,tv6)
    }
    private fun setData(md: DiaryResult) : BarData {
        var values : ArrayList<BarEntry> = arrayListOf()
        val happy = md.result.happy
        val angry = md.result.angry
        val disgust = md.result.disgust
        val fear = md.result.fear
        val neutral = md.result.neutral
        val sad = md.result.sad
        val amazed = md.result.surprise


        values.add(BarEntry(0f,happy))
        values.add(BarEntry(1f,angry))
        values.add(BarEntry(2f,disgust))
        values.add(BarEntry(3f,fear))
        values.add(BarEntry(4f,neutral))
        values.add(BarEntry(5f,sad))
        values.add(BarEntry(6f,amazed))
        var barDataset = BarDataSet(values,"")
        barDataset.setDrawIcons(false)
        barDataset.setDrawValues(false)

        var colors = arrayListOf<Int>()
        val cc = color()
        colors.add(cc.happy)
        colors.add(cc.angry)
        colors.add(cc.disgust)
        colors.add(cc.fear)
        colors.add(cc.neutral)
        colors.add(cc.sad)
        colors.add(cc.amazed)
        barDataset.setColors(colors)

        val data = BarData(barDataset)
        data.barWidth = 0.5f

        return data
    }
    private fun setEmotion(emo : Int, ptg : Float, imgView : ImageView, textView: TextView){
        val data = String.format("%.0f",ptg)
        when(emo){
            0 -> {
                imgView.setImageResource(R.drawable.ic_happy)
                textView.setText("행복 ${data}%")
            }
            1 -> {
                imgView.setImageResource(R.drawable.ic_angry)
                textView.setText("분노 ${data}%")
            }
            2 -> {
                imgView.setImageResource(R.drawable.ic_disgust)
                textView.setText("혐오 ${data}%")
            }
            3 -> {
                imgView.setImageResource(R.drawable.ic_fear)
                textView.setText("공포 ${data}%")
            }
            4 -> {
                imgView.setImageResource(R.drawable.ic_neutral)
                textView.setText("중립 ${data}%")
            }
            5 -> {
                imgView.setImageResource(R.drawable.ic_sad)
                textView.setText("슬픔 ${data}%")
            }
            6 -> {
                imgView.setImageResource(R.drawable.ic_amazed)
                textView.setText("놀람 ${data}%")
            }
        }
    }
}