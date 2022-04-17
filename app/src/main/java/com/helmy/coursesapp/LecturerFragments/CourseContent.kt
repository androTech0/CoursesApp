package com.helmy.coursesapp.LecturerFragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.helmy.coursesapp.R

class CourseContent : AppCompatActivity() {

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_content)

        val CourseId = intent.getStringExtra("CourseId").toString()

        db.collection("Courses").whereEqualTo("CourseId",CourseId).get().addOnSuccessListener {

        }



    }
}