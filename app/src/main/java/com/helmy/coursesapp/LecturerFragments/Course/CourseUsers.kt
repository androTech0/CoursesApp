package com.helmy.coursesapp.LecturerFragments.Course

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.toObjects
import com.helmy.coursesapp.Constants
import com.helmy.coursesapp.R
import com.helmy.coursesapp.UserData
import kotlinx.android.synthetic.main.activity_course_users.*

class CourseUsers : AppCompatActivity() {

    lateinit var const: Constants
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_users)
        const = Constants(this)


        getUsers(intent.getStringExtra("CourseId").toString())

    }

    private fun getUsers(CourseId: String) {

        val allUsers = ArrayList<Uuser>()

        const.db.collection("Courses").whereEqualTo("CourseId", CourseId).get()
            .addOnSuccessListener {
                val obj = it.toObjects<CourseData>()
                obj[0].StudentsIDs.forEach { UEmail ->

                    const.db.collection("users").whereEqualTo("email", UEmail).get()
                        .addOnSuccessListener { usr ->
                            val ob = usr.toObjects<UserData>()[0]
                            val fullName = ob.first_name + ob.middle_name + ob.last_name
                            allUsers.add(Uuser(ob.email, "obj.image", fullName))

                            courseUsersRec.apply {
                                adapter = CourseUsersAdapter(this@CourseUsers, allUsers)
                                layoutManager = LinearLayoutManager(this@CourseUsers)
                            }
                        }

                }



            }

    }


    data class Uuser(var email: String = "", var image: String = "", var name: String = "")


}