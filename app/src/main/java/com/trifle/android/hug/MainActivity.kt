package com.trifle.android.hug

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.trifle.android.hug.presentation.diary.DiaryActivity
import com.trifle.android.hug.presentation.home.HomeActivity
import com.trifle.android.hug.presentation.login.LoginActivity
import com.trifle.android.hug.presentation.logout.LogoutActivity
import com.trifle.android.hug.presentation.write.WriteActivity


class MainActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userBotton = findViewById<AppCompatButton>(R.id.barUser)
        val diaryButton = findViewById<AppCompatButton>(R.id.btnDiary)
        val writeButton = findViewById<AppCompatButton>(R.id.btnWrite)
        userBotton.setOnClickListener {
            val intent : Intent =  Intent(this, LogoutActivity::class.java)
            startActivity(intent)
            finish()
        }
        writeButton.setOnClickListener {
            val intent : Intent = Intent(this, WriteActivity::class.java)
            startActivity(intent)
        }
        diaryButton.setOnClickListener {
            val intent : Intent = Intent(this, DiaryActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    public override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}