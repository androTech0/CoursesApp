package com.helmy.coursesapp.LecturerFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.helmy.coursesapp.Constants
import com.helmy.coursesapp.LecturerFragments.Course.AddCourse
import com.helmy.coursesapp.LecturerFragments.Course.CourseContent
import com.helmy.coursesapp.LecturerFragments.Course.CourseData
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.courses_template.view.*
import kotlinx.android.synthetic.main.fragment_courses.*

class CoursesFragment : Fragment() {

    private var myAdapter: FirestoreRecyclerAdapter<CourseData, ViewH>? = null

    override fun onStart() {
        super.onStart()

        getAllCourses()

        AddCoursee.setOnClickListener {
            startActivity(Intent(requireContext(), AddCourse::class.java))
        }

        myAdapter!!.startListening()

    }

    private fun getAllCourses() {
        val const = Constants(requireActivity())

        val query = const.db.collection("Courses")
            .whereEqualTo("LecturerEmail", const.auth.currentUser!!.email)
        val option =
            FirestoreRecyclerOptions.Builder<CourseData>().setQuery(query, CourseData::class.java)
                .build()
        myAdapter = object : FirestoreRecyclerAdapter<CourseData, ViewH>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewH {
                val i = LayoutInflater.from(requireContext())
                    .inflate(R.layout.courses_template, parent, false)
                return ViewH(i)
            }

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(holder: ViewH, position: Int, model: CourseData) {

                holder.itemView.name.text = model.CourseName
                holder.itemView.num4videos.text = model.NumberOfVideos.toString()
                holder.itemView.num4students.text = model.NumberOfStudents.toString()
                if (model.CourseImage.isNotEmpty()) {
                    holder.itemView.image.load(model.CourseImage)
                }

                holder.itemView.setOnClickListener {
                    val i = Intent(requireContext(), CourseContent::class.java)
                    i.putExtra("CourseId", model.CourseId)
                    startActivity(i)
                }

                holder.itemView.num4videos.text = model.NumberOfVideos.toString()
            }
        }
        CoursesRecycle.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = myAdapter
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }

    override fun onStop() {
        super.onStop()
        myAdapter!!.stopListening()
    }


    class ViewH(i: View) : RecyclerView.ViewHolder(i)

}




