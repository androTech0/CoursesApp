package com.helmy.coursesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.helmy.coursesapp.LecturerFragments.AddFragment
import com.helmy.coursesapp.LecturerFragments.CoursesFragment
import com.helmy.coursesapp.LecturerFragments.EditFragment
import com.helmy.coursesapp.LecturerFragments.LecturerChattingFragment
import kotlinx.android.synthetic.main.activity_student_main.*

class StudentMainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_main)

        replaceFragment(CoursesFragment())

        studentNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.Courses ->{
                    replaceFragment(CoursesFragment())
                }
                R.id.Add ->{
                    replaceFragment(AddFragment())
                }
                R.id.Edit ->{
                    replaceFragment(EditFragment())
                }
                R.id.Chat ->{
                    replaceFragment(LecturerChattingFragment())
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