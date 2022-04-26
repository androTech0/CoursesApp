package com.helmy.coursesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.helmy.coursesapp.Log.Login
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        btn1.setOnClickListener {
            startActivity(Intent(this,Login::class.java))
            finish()
        }
    }
}