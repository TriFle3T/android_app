package com.trifle.android.hug

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.trifle.android.hug.presentation.login.LoginActivity


class MainActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        findViewById<TextView>(R.id.helloworldTextView).setOnClickListener {
//            startActivity(Intent(this, LoginActivity::class.java))
//        }
    }

    public override fun onStart() {
        super.onStart()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
//        else {
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }
    }
}