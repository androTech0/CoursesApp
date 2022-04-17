package com.helmy.coursesapp.LecturerFragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.helmy.coursesapp.R

class CourseContent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_content)

        val courseId = intent.getStringExtra("CourseId").toString()

    }
}