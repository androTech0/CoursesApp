package com.helmy.coursesapp.Lecturer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.helmy.coursesapp.Lecturer.Fragments.LecturerCoursesFragment
import com.helmy.coursesapp.Lecturer.Fragments.LecturerEditCourseFragment
import com.helmy.coursesapp.Lecturer.Fragments.LecturerChattingFragment
import com.helmy.coursesapp.Lecturer.Fragments.LecturerProfileFragment
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.activity_lecturer_main.*

class LecturerMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecturer_main)

        replaceFragment(LecturerCoursesFragment())

        LecturerNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.Courses_ ->{
                    replaceFragment(LecturerCoursesFragment())
                }
                R.id.Edit ->{
                    replaceFragment(LecturerEditCourseFragment())
                }
                R.id.Chat_ ->{
                    replaceFragment(LecturerChattingFragment())
                }
                R.id.profile_ ->{
                    replaceFragment(LecturerProfileFragment())
                }
            }
            true
        }

    }

    private fun replaceFragment(fragment:Fragment){
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.LecturerFrameLayout,fragment)
        transaction.commit()
    }
}