package com.helmy.coursesapp.Lecturer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.helmy.coursesapp.Lecturer.Fragments.CoursesFragment
import com.helmy.coursesapp.Lecturer.Fragments.EditFragment
import com.helmy.coursesapp.Lecturer.Fragments.LecturerChattingFragment
import com.helmy.coursesapp.Lecturer.Fragments.Profile2LecturerFragment
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.activity_lecturer_main.*

class LecturerMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecturer_main)

        replaceFragment(CoursesFragment())

        LecturerNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.Courses_ ->{
                    replaceFragment(CoursesFragment())
                }
                R.id.Edit ->{
                    replaceFragment(EditFragment())
                }
                R.id.Chat_ ->{
                    replaceFragment(LecturerChattingFragment())
                }
                R.id.profile_ ->{
                    replaceFragment(Profile2LecturerFragment())
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