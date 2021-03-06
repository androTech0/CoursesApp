package com.helmy.coursesapp.Lecturer.Course

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.toObjects
import com.helmy.coursesapp.ChattingActivity
import com.helmy.coursesapp.Classes.CourseData
import com.helmy.coursesapp.Classes.UsersChattedAdapter
import com.helmy.coursesapp.Classes.Constants
import com.helmy.coursesapp.R
import com.helmy.coursesapp.Classes.UserData
import com.helmy.coursesapp.Classes.Uuser
import kotlinx.android.synthetic.main.activity_course_users.*

class CourseUsers : AppCompatActivity() {

    lateinit var const: Constants

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_users)
        const = Constants(this)

        val courseId = intent.getStringExtra("CourseId").toString()

        getUsers(courseId)

        Msg2Group.setOnClickListener {
            val i = Intent(this, ChattingActivity::class.java)
            i.putExtra("ReceiverEmail",courseId)
            startActivity(i)
        }

    }

    private fun getUsers(CourseId: String) {

        val allUsers = ArrayList<Uuser>()

        const.db.collection("Courses").whereEqualTo("CourseId", CourseId).get()
            .addOnSuccessListener {
                val obj = it.toObjects<CourseData>()
                obj[0].StudentsIDs.forEach { UEmail ->

                    const.db.collection("Users").whereEqualTo("Email", UEmail).get()
                        .addOnSuccessListener { usr ->
                            val ob = usr.toObjects<UserData>()[0]
                            allUsers.add(Uuser(ob.Email, ob.Image, ob.Name))

                            courseUsersRec.apply {
                                adapter = UsersChattedAdapter(this@CourseUsers, allUsers)
                                layoutManager = LinearLayoutManager(this@CourseUsers)
                            }
                        }
                }
            }
    }
}