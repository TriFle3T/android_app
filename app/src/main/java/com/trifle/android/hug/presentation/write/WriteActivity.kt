package com.trifle.android.hug.presentation.write

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.trifle.android.hug.R
import com.trifle.android.hug.presentation.home.HomeActivity
import com.trifle.android.hug.presentation.results.ResultActivity

class WriteActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        val backButton = findViewById<AppCompatButton>(R.id.btnBack)
        val submitButton = findViewById<AppCompatButton>(R.id.btnSubmit)

        backButton.setOnClickListener {
            finish()
        }
        submitButton.setOnClickListener {
            val intent : Intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}