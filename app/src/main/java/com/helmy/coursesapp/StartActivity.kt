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

        val shared = getSharedPreferences("CoursesApp", MODE_PRIVATE)
        val isLogged = shared.getBoolean("isLogged",false)
        if (isLogged){
            startActivity(Intent(this,Login::class.java))
            finish()
        }
        btn1.setOnClickListener {
            shared.edit().putBoolean("isLogged",true).apply()
            startActivity(Intent(this,Login::class.java))
            finish()

        }
    }
}