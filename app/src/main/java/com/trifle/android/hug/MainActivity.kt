package com.trifle.android.hug

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.trifle.android.hug.presentation.home.HomeActivity
import com.trifle.android.hug.presentation.login.LoginActivity
import com.trifle.android.hug.presentation.logout.LogoutActivity


class MainActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        findViewById<TextView>(R.id.helloworldTextView).setOnClickListener {
//            startActivity(Intent(this, LoginActivity::class.java))
//        }
        val userBotton = findViewById<AppCompatButton>(R.id.barUser)
        userBotton.setOnClickListener {
            val intent : Intent =  Intent(this, LogoutActivity::class.java)
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