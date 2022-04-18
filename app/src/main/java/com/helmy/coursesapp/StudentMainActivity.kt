package com.helmy.coursesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.helmy.coursesapp.LecturerFragments.AddCourse
import com.helmy.coursesapp.LecturerFragments.CoursesFragment
import com.helmy.coursesapp.LecturerFragments.EditFragment
import com.helmy.coursesapp.LecturerFragments.LecturerChattingFragment
import com.helmy.coursesapp.StudentFragments.HomeFragment
import com.helmy.coursesapp.StudentFragments.ProfileFragment
import com.helmy.coursesapp.StudentFragments.SettingsFragment
import com.helmy.coursesapp.StudentFragments.StudentChattingFragment
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
                    replaceFragment(SettingsFragment())
                }
                R.id.Settings ->{
                    replaceFragment(ProfileFragment())
                }
                R.id.Profile ->{
                    replaceFragment(StudentChattingFragment())
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