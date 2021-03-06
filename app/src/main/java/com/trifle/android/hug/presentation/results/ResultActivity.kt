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
            //Q ?????? ????????? ??????. (??????????????? 10, API 29 ????????? ??????)
            saveImageOnAboveAndroidQ(bitmap)
            Toast.makeText(baseContext, "????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
        } else {
            // Q ?????? ????????? ??????. ????????? ????????? ????????????.
            val writePermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if(writePermission == PackageManager.PERMISSION_GRANTED) {
                saveImageOnUnderAndroidQ(bitmap)
                Toast.makeText(baseContext, "????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
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
            dir.mkdirs() // ?????? ???????????? ?????? ??????
        }

        try {
            val fileItem = File("$dir/$fileName")
            fileItem.createNewFile()
            //0KB ?????? ??????.

            val fos = FileOutputStream(fileItem) // ?????? ????????? ?????????

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            //?????? ????????? ????????? ????????? ????????? Bitmap ??????.

            fos.close() // ?????? ????????? ????????? ?????? close

            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileItem)))
            // ?????????????????? ??????????????? ?????? ????????? ?????? ?????? ??????. ????????? ???????????? ????????? ????????? Uri??? ????????????.
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun drawBitmap(view: View): Bitmap {
        //?????? ???????????? ?????????.
        val backgroundWidth = resources.displayMetrics.widthPixels
        val backgroundHeight = resources.displayMetrics.heightPixels

        val totalBitmap = Bitmap.createBitmap(backgroundWidth, backgroundHeight, Bitmap.Config.ARGB_8888) // ????????? ??????
        val canvas = Canvas(totalBitmap) // ???????????? ???????????? Mapping.

//        val bgColor = binding.viewModel?.background?.value // ???????????? ?????? ????????? ???????????? ????????????.
        val bgColor = R.color.background_color
        if(bgColor != null) {
            val color = ContextCompat.getColor(baseContext, bgColor)
            canvas.drawColor(color) // ???????????? ?????? ????????? ????????????????????? ?????????.
        }

        val imageView = view
        val imageViewBitmap = Bitmap.createBitmap(imageView.width, imageView.height, Bitmap.Config.ARGB_8888)
        val imageViewCanvas = Canvas(imageViewBitmap)
        imageView.draw(imageViewCanvas)
        /*imageViewCanvas??? ????????? imageView??? ?????????.
         *??? ??? ??????????????? imageViewBitmap????????? imageViewBitmap??? imageView??? ????????????.
         */

        val imageViewLeft = ((backgroundWidth - imageView.width) / 2).toFloat()
        val imageViewTop = ((backgroundHeight - imageView.height) / 2).toFloat()
        /*???????????? ????????? ??? ??????. ??? ???????????? ImageView??? ???????????????.
        * ????????? ???????????? - ???????????? ???????????? ??? 2??? ?????? ??? ????????? ?????? ???????????? ????????? ??????.
        * ?????? ????????? ??????????????? ???????????????.
        * */

        canvas.drawBitmap(imageViewBitmap, imageViewLeft, imageViewTop, null)

        //????????? TextView. ?????? ImageView??? ?????? ???????????? ??????????????? ?????? ??? ???????????? ????????????.
//        val textView = binding.tv
//        if(textView.length() > 0) {
//            //textView??? ????????? ????????????
//            val textViewBitmap = Bitmap.createBitmap(textView.width, textView.height, Bitmap.Config.ARGB_8888)
//            val textViewCanvas = Canvas(textViewBitmap)
//            textView.draw(textViewCanvas)
//
//            val marginTop = (20 * resources.displayMetrics.density).toInt() // 20dp??? ??????
//            val textViewLeft = ((backgroundWidth - textView.width) / 2).toFloat()
//            val textViewTop = imageViewTop + imageView.height + marginTop
//
//            canvas.drawBitmap(textViewBitmap, textViewLeft, textViewTop, null)
//        }

        return totalBitmap
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageOnAboveAndroidQ(bitmap: Bitmap) {
        val fileName = System.currentTimeMillis().toString() + ".png" // ???????????? ????????????.png

        /*
        * ContentValues() ?????? ??????.
        * ContentValues??? ContentResolver??? ????????? ??? ?????? ?????? ???????????? ???????????? ????????????.
        * */
        val contentValues = ContentValues()
        contentValues.apply {
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/ImageSave") // ?????? ??????
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName) // ??????????????? put?????????.
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.IS_PENDING, 1) // ?????? is_pending ???????????? ???????????????.
            // ?????? ????????? ??? ???????????? ???????????? ??????????????? ?????????, ?????? ???????????? ????????? ??? ??????.
        }

        // ???????????? ????????? uri??? ?????? ??????????????????.
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            if(uri != null) {
                val image = contentResolver.openFileDescriptor(uri, "w", null)
                // write ????????? file??? open??????.

                if(image != null) {
                    val fos = FileOutputStream(image.fileDescriptor)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    //???????????? FileOutputStream??? ?????? compress??????.
                    fos.close()

                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0) // ????????? ????????? ????????????.
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
                textView.setText("?????? ${data}%")
            }
            1 -> {
                imgView.setImageResource(R.drawable.ic_angry)
                textView.setText("?????? ${data}%")
            }
            2 -> {
                imgView.setImageResource(R.drawable.ic_disgust)
                textView.setText("?????? ${data}%")
            }
            3 -> {
                imgView.setImageResource(R.drawable.ic_fear)
                textView.setText("?????? ${data}%")
            }
            4 -> {
                imgView.setImageResource(R.drawable.ic_neutral)
                textView.setText("?????? ${data}%")
            }
            5 -> {
                imgView.setImageResource(R.drawable.ic_sad)
                textView.setText("?????? ${data}%")
            }
            6 -> {
                imgView.setImageResource(R.drawable.ic_amazed)
                textView.setText("?????? ${data}%")
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