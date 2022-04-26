package com.helmy.coursesapp.Student

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.helmy.coursesapp.R
import com.helmy.coursesapp.Student.Fragments.AllCoursesFragment
import com.helmy.coursesapp.Student.Fragments.StudentProfileFragment
import com.helmy.coursesapp.Student.Fragments.RegesteredCoursesFragment
import com.helmy.coursesapp.Student.Fragments.StudentChattingFragment
import kotlinx.android.synthetic.main.activity_student_main.*

class StudentMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_main)

        replaceFragment(AllCoursesFragment())

        studentNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.Home ->{
                    replaceFragment(AllCoursesFragment())
                }
                R.id.ChatS ->{
                    replaceFragment(StudentChattingFragment())
                }
                R.id.Coursess ->{
                    replaceFragment(RegesteredCoursesFragment())
                }
                R.id.Profile ->{
                    replaceFragment(StudentProfileFragment())
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