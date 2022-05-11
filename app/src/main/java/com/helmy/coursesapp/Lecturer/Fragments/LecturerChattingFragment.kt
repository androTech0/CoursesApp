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

        val arra = ArrayList<Uuser>()
        val currentUserEmail = const.auth.currentUser!!.email

        try {

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                arra.clear()
                snapshot.children.forEach {

                    val obj = it.getValue(MsgClass::class.java)!!
                    if (obj.receiver == currentUserEmail ) {

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
                    }else if (obj.sender == currentUserEmail){

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
                                    adapter = UsersChattedAdapter(requireActivity(), arra)
                                    layoutManager = LinearLayoutManager(requireContext())
                                }


                            }

                    }else {
                        const.db.collection("Courses").get().addOnSuccessListener { courses ->
                            courses.forEach { currentCourse ->
                                if (currentUserEmail == currentCourse.getString("LecturerEmail")) {
                                    if (!arra.contains(
                                            Uuser(
                                                currentCourse.get("CourseId").toString(),
                                                currentCourse.get("CourseImage").toString(),
                                                currentCourse.get("CourseName").toString()
                                            )
                                        )
                                    )
                                        arra.add(
                                            Uuser(
                                                currentCourse.get("CourseId").toString(),
                                                currentCourse.get("CourseImage").toString(),
                                                currentCourse.get("CourseName").toString()
                                            )
                                        )

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

    }catch (e :Exception){
        Toast.makeText(requireContext(), "Exception = $e", Toast.LENGTH_SHORT).show()
    }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lecturer_chatting, container, false)
    }

}