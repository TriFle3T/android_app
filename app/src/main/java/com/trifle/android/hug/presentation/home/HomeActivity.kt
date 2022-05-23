package com.trifle.android.hug.presentation.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.trifle.android.hug.R
import com.trifle.android.hug.presentation.logout.LogoutActivity

class HomeActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val userBotton = findViewById<AppCompatButton>(R.id.barUser)
        userBotton.setOnClickListener {
            val intent : Intent =  Intent(this, LogoutActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}