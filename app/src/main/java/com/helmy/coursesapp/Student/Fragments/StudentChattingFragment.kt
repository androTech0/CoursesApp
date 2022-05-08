package com.helmy.coursesapp.Student.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.helmy.coursesapp.Classes.Constants
import com.helmy.coursesapp.Classes.MsgClass
import com.helmy.coursesapp.Classes.UsersChattedAdapter
import com.helmy.coursesapp.Classes.Uuser
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.fragment_student_chatting.*


class StudentChattingFragment : Fragment() {

    override fun onStart() {
        super.onStart()

        getAllMessages()

    }

    private fun getAllMessages() {

        val const = Constants(requireContext())
        val db = const.rtdb.child("Chats")

        val arra = ArrayList<Uuser>()
        val currentUserEmail = const.auth.currentUser!!.email

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                arra.clear()
                snapshot.children.forEach {

                    val obj = it.getValue(MsgClass::class.java)!!
                    if (obj.receiver == currentUserEmail) {

                        const.db.collection("Users").document(obj.sender).get()
                            .addOnSuccessListener { i ->

                                if (!arra.contains(Uuser(obj.sender,   i.get("Image").toString(), i.get("Name").toString())))
                                    arra.add(Uuser(obj.sender,   i.get("Image").toString(), i.get("Name").toString()))

                                UserChatRecycle.apply {
                                    adapter = UsersChattedAdapter(requireContext(), arra)
                                    layoutManager = LinearLayoutManager(requireContext())
                                }
                            }
                    } else {
                        const.db.collection("Courses").get().addOnSuccessListener { courses ->
                            courses.forEach { currentCourse ->
                                val students = currentCourse.get("StudentsIDs") as ArrayList<String>
                                if (students.contains(currentUserEmail)) {
                                    if (!arra.contains(Uuser(currentCourse.get("CourseId").toString(), currentCourse.get("CourseImage").toString(), currentCourse.get("CourseName").toString())))
                                        arra.add(Uuser(currentCourse.get("CourseId").toString(), currentCourse.get("CourseImage").toString(), currentCourse.get("CourseName").toString()))

                                    UserChatRecycle.apply {
                                        adapter = UsersChattedAdapter(requireContext(), arra)
                                        layoutManager = LinearLayoutManager(requireContext())
                                    }
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_student_chatting, container, false)
    }

}