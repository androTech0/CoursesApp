package com.helmy.coursesapp.Student.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.helmy.coursesapp.Classes.Constants
import com.helmy.coursesapp.Classes.CourseData
import com.helmy.coursesapp.Lecturer.Fragments.LecturerCoursesFragment
import com.helmy.coursesapp.R
import com.helmy.coursesapp.Student.Video2Student
import kotlinx.android.synthetic.main.courses_fragment.*
import kotlinx.android.synthetic.main.courses_template.view.*


class RegesteredCoursesFragment : Fragment() {

    lateinit var const: Constants
    private var myAdapter: FirestoreRecyclerAdapter<CourseData, LecturerCoursesFragment.ViewH>? = null

    override fun onStart() {
        super.onStart()

        const = Constants(requireContext())
        getAllCourses()

        myAdapter!!.startListening()

    }

    private fun getAllCourses() {
        val currentUEmail = const.auth.currentUser!!.email
        val query =
            const.db.collection("Courses").whereArrayContains("StudentsIDs", currentUEmail!!)

        val option =
            FirestoreRecyclerOptions.Builder<CourseData>().setQuery(query, CourseData::class.java)
                .build()
        myAdapter = object : FirestoreRecyclerAdapter<CourseData, LecturerCoursesFragment.ViewH>(option) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): LecturerCoursesFragment.ViewH {
                val i = LayoutInflater.from(requireContext())
                    .inflate(R.layout.courses_template, parent, false)
                return LecturerCoursesFragment.ViewH(i)
            }

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(
                holder: LecturerCoursesFragment.ViewH,
                position: Int,
                model: CourseData
            ) {
                holder.itemView.name.text = model.CourseName
                holder.itemView.num4videos.text = model.NumberOfVideos.toString()
                holder.itemView.num4students.text = model.NumberOfStudents.toString()

                if (model.CourseImage.isNotEmpty()) {
                    holder.itemView.image.load(model.CourseImage)
                }

                holder.itemView.setOnClickListener {
                    val i = Intent(requireContext(), Video2Student::class.java)
                    i.putExtra("CourseId", model.CourseId)
                    requireContext().startActivity(i)
                }


            }
        }
        regesterdRecycle.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = myAdapter
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.courses_fragment, container, false)
    }

}