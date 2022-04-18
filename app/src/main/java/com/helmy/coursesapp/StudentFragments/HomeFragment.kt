package com.helmy.coursesapp.StudentFragments

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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.helmy.coursesapp.LecturerFragments.Course.CourseContent
import com.helmy.coursesapp.LecturerFragments.Course.CourseData
import com.helmy.coursesapp.LecturerFragments.CoursesFragment
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.courses_template.view.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private var db = Firebase.firestore
    private var myAdapter: FirestoreRecyclerAdapter<CourseData, CoursesFragment.ViewH>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStart() {
        super.onStart()

        getAllCourses()

        myAdapter!!.startListening()
    }

    private fun getAllCourses() {

        val query = db.collection("Courses")
        val option =
            FirestoreRecyclerOptions.Builder<CourseData>().setQuery(query, CourseData::class.java)
                .build()
        myAdapter = object : FirestoreRecyclerAdapter<CourseData, CoursesFragment.ViewH>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoursesFragment.ViewH {
                val i = LayoutInflater.from(requireContext())
                    .inflate(R.layout.courses_template, parent, false)
                return CoursesFragment.ViewH(i)
            }

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(holder: CoursesFragment.ViewH, position: Int, model: CourseData) {

                holder.itemView.name.text = model.CourseName
                if (model.CourseImage.isNotEmpty()) {
                    holder.itemView.image.load(model.CourseImage)
                }

                holder.itemView.setOnClickListener {
                    val i = Intent(requireContext(), CourseContent::class.java)
                    i.putExtra("CourseId", model.CourseId)
                    startActivity(i)
                }
            }
        }
        CoursesRecycle.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = myAdapter
        }

    }

}