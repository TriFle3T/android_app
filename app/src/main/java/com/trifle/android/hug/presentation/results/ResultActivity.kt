package com.trifle.android.hug.presentation.results

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.trifle.android.hug.MainActivity
import com.trifle.android.hug.R
import com.trifle.android.hug.data.database.userDb
import com.trifle.android.hug.data.entity.token
import com.trifle.android.hug.presentation.color
import retrofit2.Retrofit
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ResultActivity  : AppCompatActivity() {
    private var rtf : Retrofit? = null
    private var db : userDb? = null
    private var tkList : List<token>? = null
    private var happy : Float ? = null
    private var angry : Float ? = null
    private var disgust : Float ? = null
    private var fear : Float ? = null
    private var amazed : Float ? = null
    private var sad : Float ? = null
    private var neutral : Float ? = null

    @SuppressLint("SetTextI18n")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val metaData = intent
        val homeButton = findViewById<AppCompatButton>(R.id.btnHome)
        val downButton = findViewById<AppCompatButton>(R.id.btnDown)
        val captureTarget = findViewById<ConstraintLayout>(R.id.capture_target)
        val tvQuote = findViewById<TextView>(R.id.tvContent)
        val bcEmotion = findViewById<HorizontalBarChart>(R.id.bcEmotion)
        tvQuote?.bringToFront()

        homeButton.setOnClickListener {
            val intent : Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        val sdf = SimpleDateFormat("yyyyMMddHHmmss");
        val time = Date();
        val current_time = sdf.format(time);

        downButton.setOnClickListener {
            requestCapture(captureTarget,current_time+"_capture")
        }
//        metaData.getFloatExtra("happy",0.0f)
        val content = metaData.getStringExtra("content")
        val speaker = metaData.getStringExtra("speaker")
        tvQuote.text = "${content}\n-${speaker}-"

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

        bcEmotion.data = setData(metaData)
        bcEmotion.invalidate()
        val img = findViewById<ImageView>(R.id.img) ; val tv = findViewById<TextView>(R.id.tv)
        val img1 = findViewById<ImageView>(R.id.img1) ; val tv1 = findViewById<TextView>(R.id.tv1)
        val img2 = findViewById<ImageView>(R.id.img2) ; val tv2 = findViewById<TextView>(R.id.tv2)
        val img3 = findViewById<ImageView>(R.id.img3) ; val tv3 = findViewById<TextView>(R.id.tv3)
        val img4 = findViewById<ImageView>(R.id.img4) ; val tv4 = findViewById<TextView>(R.id.tv4)
        val img5 = findViewById<ImageView>(R.id.img5) ; val tv5 = findViewById<TextView>(R.id.tv5)
        val img6 = findViewById<ImageView>(R.id.img6) ; val tv6 = findViewById<TextView>(R.id.tv6)
        setEmotion(6,amazed!!,img,tv)
        setEmotion(5,sad!!,img1,tv1)
        setEmotion(4,neutral!!,img2,tv2)
        setEmotion(3,fear!!,img3,tv3)
        setEmotion(2,disgust!!,img4,tv4)
        setEmotion(1,angry!!,img5,tv5)
        setEmotion(0,happy!!,img6,tv6)
    }
    private fun requestCapture(view : View, title : String){
//        if(view==null){
//            Log.d("capture Error",title.toString())
//            return
//        }
//        view.buildDrawingCache()
//        val bitmap = view.getDrawingCache()
//        val fos : FileOutputStream
//        val uploadFolder = Environment.getExternalStoragePublicDirectory("/DCIM/Camera/")
//        if(!uploadFolder.exists())
//        {
//            uploadFolder.mkdir()
//        }
//        val Str_Path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera/"
//        try {
//            fos = FileOutputStream(Str_Path+title+".jpg")
//            bitmap.compress(Bitmap.CompressFormat.JPEG,80,fos)
//        }catch (e : Exception){
//            e.printStackTrace()
//        }
        val bitmap = drawBitmap(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //Q 버전 이상일 경우. (안드로이드 10, API 29 이상일 경우)
            saveImageOnAboveAndroidQ(bitmap)
            Toast.makeText(baseContext, "이미지 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            // Q 버전 이하일 경우. 저장소 권한을 얻어온다.
            val writePermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if(writePermission == PackageManager.PERMISSION_GRANTED) {
                saveImageOnUnderAndroidQ(bitmap)
                Toast.makeText(baseContext, "이미지 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                val requestExternalStorageCode = 1

                val permissionStorage = arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

                ActivityCompat.requestPermissions(this, permissionStorage, requestExternalStorageCode)
            }
        }

    }
    private fun saveImageOnUnderAndroidQ(bitmap: Bitmap) {
        val fileName = System.currentTimeMillis().toString() + ".png"
        val externalStorage = Environment.getExternalStorageDirectory().absolutePath
        val path = "$externalStorage/DCIM/imageSave"
        val dir = File(path)

        if(dir.exists().not()) {
            dir.mkdirs() // 폴더 없을경우 폴더 생성
        }

        try {
            val fileItem = File("$dir/$fileName")
            fileItem.createNewFile()
            //0KB 파일 생성.

            val fos = FileOutputStream(fileItem) // 파일 아웃풋 스트림

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            //파일 아웃풋 스트림 객체를 통해서 Bitmap 압축.

            fos.close() // 파일 아웃풋 스트림 객체 close

            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileItem)))
            // 브로드캐스트 수신자에게 파일 미디어 스캔 액션 요청. 그리고 데이터로 추가된 파일에 Uri를 넘겨준다.
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun drawBitmap(view: View): Bitmap {
        //기기 해상도를 가져옴.
        val backgroundWidth = resources.displayMetrics.widthPixels
        val backgroundHeight = resources.displayMetrics.heightPixels

        val totalBitmap = Bitmap.createBitmap(backgroundWidth, backgroundHeight, Bitmap.Config.ARGB_8888) // 비트맵 생성
        val canvas = Canvas(totalBitmap) // 캔버스에 비트맵을 Mapping.

//        val bgColor = binding.viewModel?.background?.value // 뷰모델의 현재 설정된 배경색을 가져온다.
        val bgColor = R.color.background_color
        if(bgColor != null) {
            val color = ContextCompat.getColor(baseContext, bgColor)
            canvas.drawColor(color) // 캔버스에 현재 설정된 배경화면색으로 칠한다.
        }

        val imageView = view
        val imageViewBitmap = Bitmap.createBitmap(imageView.width, imageView.height, Bitmap.Config.ARGB_8888)
        val imageViewCanvas = Canvas(imageViewBitmap)
        imageView.draw(imageViewCanvas)
        /*imageViewCanvas를 통해서 imageView를 그린다.
         *이 때 스케치북은 imageViewBitmap이므로 imageViewBitmap에 imageView가 그려진다.
         */

        val imageViewLeft = ((backgroundWidth - imageView.width) / 2).toFloat()
        val imageViewTop = ((backgroundHeight - imageView.height) / 2).toFloat()
        /*이미지가 그려질 곳 계산. 정 가운데에 ImageView를 그릴것이다.
        * 기기의 가로크기 - 이미지의 가로크기 를 2로 나눈 후 왼쪽에 해당 크기만큼 마진을 준다.
        * 세로 크기도 마찬가지로 계산해준다.
        * */

        canvas.drawBitmap(imageViewBitmap, imageViewLeft, imageViewTop, null)

        //아래는 TextView. 위에 ImageView와 같은 로직으로 비트맵으로 만든 후 캔버스에 그려준다.
//        val textView = binding.tv
//        if(textView.length() > 0) {
//            //textView가 공백이 아닐때만
//            val textViewBitmap = Bitmap.createBitmap(textView.width, textView.height, Bitmap.Config.ARGB_8888)
//            val textViewCanvas = Canvas(textViewBitmap)
//            textView.draw(textViewCanvas)
//
//            val marginTop = (20 * resources.displayMetrics.density).toInt() // 20dp의 마진
//            val textViewLeft = ((backgroundWidth - textView.width) / 2).toFloat()
//            val textViewTop = imageViewTop + imageView.height + marginTop
//
//            canvas.drawBitmap(textViewBitmap, textViewLeft, textViewTop, null)
//        }

        return totalBitmap
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageOnAboveAndroidQ(bitmap: Bitmap) {
        val fileName = System.currentTimeMillis().toString() + ".png" // 파일이름 현재시간.png

        /*
        * ContentValues() 객체 생성.
        * ContentValues는 ContentResolver가 처리할 수 있는 값을 저장해둘 목적으로 사용된다.
        * */
        val contentValues = ContentValues()
        contentValues.apply {
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/ImageSave") // 경로 설정
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName) // 파일이름을 put해준다.
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.IS_PENDING, 1) // 현재 is_pending 상태임을 만들어준다.
            // 다른 곳에서 이 데이터를 요구하면 무시하라는 의미로, 해당 저장소를 독점할 수 있다.
        }

        // 이미지를 저장할 uri를 미리 설정해놓는다.
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            if(uri != null) {
                val image = contentResolver.openFileDescriptor(uri, "w", null)
                // write 모드로 file을 open한다.

                if(image != null) {
                    val fos = FileOutputStream(image.fileDescriptor)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    //비트맵을 FileOutputStream를 통해 compress한다.
                    fos.close()

                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0) // 저장소 독점을 해제한다.
                    contentResolver.update(uri, contentValues, null, null)
                }
            }
        } catch(e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
    private fun setData(md : Intent) : BarData{
        var values : ArrayList<BarEntry> = arrayListOf()
        happy = md.getFloatExtra("happy",-1f)
        angry = md.getFloatExtra("angry",-1f)
        disgust = md.getFloatExtra("disgust",-1f)
        fear = md.getFloatExtra("fear",-1f)
        sad = md.getFloatExtra("sad",-1f)
        neutral = md.getFloatExtra("neutral",-1f)
        amazed= md.getFloatExtra("amazed",-1f)

        values.add(BarEntry(0f,happy!!))
        values.add(BarEntry(1f,angry!!))
        values.add(BarEntry(2f,disgust!!))
        values.add(BarEntry(3f,fear!!))
        values.add(BarEntry(4f,neutral!!))
        values.add(BarEntry(5f,sad!!))
        values.add(BarEntry(6f,amazed!!))
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

}