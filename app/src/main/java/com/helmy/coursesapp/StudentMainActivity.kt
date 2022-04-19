package com.helmy.coursesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
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

                    replaceFragment(StudentChattingFragment())
                }
                R.id.Settings ->{
                    replaceFragment(SettingsFragment())
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