package com.helmy.coursesapp.Student

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.helmy.coursesapp.R
import com.helmy.coursesapp.Student.Fragments.HomeFragment
import com.helmy.coursesapp.Student.Fragments.ProfileFragment
import com.helmy.coursesapp.Student.Fragments.RegesteredCourses
import com.helmy.coursesapp.Student.Fragments.StudentChattingFragment
import kotlinx.android.synthetic.main.activity_student_main.*

class StudentMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_main)

        replaceFragment(HomeFragment())

        studentNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.Home ->{
                    replaceFragment(HomeFragment())
                }
                R.id.ChatS ->{
                    replaceFragment(StudentChattingFragment())
                }
                R.id.Coursess ->{
                    replaceFragment(RegesteredCourses())
                }
                R.id.Profile ->{
                    replaceFragment(ProfileFragment())
                }
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.studentFrameLayout,fragment)
        transaction.commit()
    }

}