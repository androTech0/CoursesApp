package com.helmy.coursesapp.Lecturer.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.helmy.coursesapp.Classes.*
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




        val currentUserEmail = const.auth.currentUser!!.email.toString()

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val arra = ArrayList<Uuser>()
                val courses = ArrayList<String>()

                snapshot.children.forEach {

                    val obj = it.getValue(MsgClass::class.java)!!
                    if (!obj.receiver.contains("@gmail.com"))
                        courses.add(obj.receiver)
                }

                snapshot.children.forEach {

                    val obj = it.getValue(MsgClass::class.java)!!

                    when {

                        obj.receiver == currentUserEmail -> {

                            const.db.collection("Users").document(obj.sender).get()
                                .addOnSuccessListener { i ->
                                    if (!arra.contains(
                                            Uuser(
                                                obj.sender,
                                                i.get("Image").toString(),
                                                i.get("Name").toString()
                                            )
                                        )
                                    )
                                        arra.add(
                                            Uuser(
                                                obj.sender,
                                                i.get("Image").toString(),
                                                i.get("Name").toString()
                                            )
                                        )

                                    chattingRecycleLecturer.apply {
                                        adapter = UsersChattedAdapter(requireContext(), arra)
                                        layoutManager = LinearLayoutManager(requireContext())
                                    }
                                }
                        }

                        courses.contains(obj.receiver) -> {
                            const.db.collection("Courses").whereEqualTo("CourseId", obj.receiver)
                                .get()
                                .addOnSuccessListener { courses ->

                                    if (!arra.contains(
                                            Uuser(
                                                courses.documents[0].get("CourseId").toString(),
                                                courses.documents[0].get("CourseImage").toString(),
                                                courses.documents[0].get("CourseName").toString()
                                            )
                                        )
                                    ) {

                                        arra.add(
                                            Uuser(
                                                courses.documents[0].get("CourseId").toString(),
                                                courses.documents[0].get("CourseImage").toString(),
                                                courses.documents[0].get("CourseName").toString()
                                            )
                                        )

                                    }
                                    chattingRecycleLecturer.apply {
                                        adapter = UsersChattedAdapter(requireContext(), arra)
                                        layoutManager = LinearLayoutManager(requireContext())

                                    }
                                }
                        }

                        obj.sender == currentUserEmail -> {

                            const.db.collection("Users").document(obj.receiver).get()
                                .addOnSuccessListener { i ->
                                    if (!arra.contains(
                                            Uuser(
                                                obj.receiver,
                                                i.get("Image").toString(),
                                                i.get("Name").toString()
                                            )
                                        )
                                    )
                                        arra.add(
                                            Uuser(
                                                obj.receiver,
                                                i.get("Image").toString(),
                                                i.get("Name").toString()
                                            )
                                        )

                                    chattingRecycleLecturer.apply {
                                        adapter = UsersChattedAdapter(requireContext(), arra)
                                        layoutManager = LinearLayoutManager(requireContext())
                                    }
                                }
                        }

                        else -> {
                            Toast.makeText(requireContext(), "else", Toast.LENGTH_SHORT).show()
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