package com.helmy.coursesapp.Lecturer.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.helmy.coursesapp.Constants
import com.helmy.coursesapp.ChattingActivity
import com.helmy.coursesapp.Classes.MsgClass
import com.helmy.coursesapp.Classes.UsersChattedAdapter
import com.helmy.coursesapp.Classes.Uuser
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.fragment_lecturer_chatting.*

class LecturerChattingFragment : Fragment() {


    override fun onStart() {
        super.onStart()

        getAllUsers()

    }

    private fun getAllUsers() {

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

                        const.db.collection("users").document(obj.sender).get()
                            .addOnSuccessListener { i ->
                                val fullName =
                                    i.getString("first_name") + i.getString("middle_name") + i.getString(
                                        "last_name"
                                    )
                                if (!arra.contains(Uuser(obj.sender, "", fullName)))
                                    arra.add(Uuser(obj.sender, "", fullName))

                                chattingRecycleLecturer.apply {
                                    adapter = UsersChattedAdapter(requireContext(), arra)
                                    layoutManager = LinearLayoutManager(requireContext())
                                }
                            }
                    } else {
                        const.db.collection("Courses").get().addOnSuccessListener { courses ->
                            courses.forEach { currentCourse ->
                                if (currentUserEmail == currentCourse.getString("LecturerEmail")){
                                    if (!arra.contains(Uuser(currentCourse.get("CourseId").toString(), "", currentCourse.get("CourseName").toString())))
                                        arra.add(Uuser(currentCourse.get("CourseId").toString(), "", currentCourse.get("CourseName").toString()))

                                    chattingRecycleLecturer.apply {
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
        return inflater.inflate(R.layout.fragment_lecturer_chatting, container, false)
    }

}