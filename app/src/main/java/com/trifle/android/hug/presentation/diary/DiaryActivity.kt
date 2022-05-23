package com.trifle.android.hug.presentation.diary

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.trifle.android.hug.R
import com.trifle.android.hug.presentation.home.HomeActivity
import com.trifle.android.hug.presentation.logout.LogoutActivity
import com.trifle.android.hug.presentation.write.WriteActivity

class DiaryActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)
        val userBotton = findViewById<AppCompatButton>(R.id.barUser)
        val homeButton = findViewById<AppCompatButton>(R.id.btnHome)
        val writeButton = findViewById<AppCompatButton>(R.id.btnWrite)

        userBotton.setOnClickListener {
            val intent : Intent =  Intent(this, LogoutActivity::class.java)
            startActivity(intent)
            finish()
        }
        homeButton.setOnClickListener {
            val intent : Intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        writeButton.setOnClickListener {
            val intent : Intent = Intent(this, WriteActivity::class.java)
            startActivity(intent)
        }

    }
}