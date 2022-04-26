package com.helmy.coursesapp.Student.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ktx.toObject
import com.helmy.coursesapp.Constants
import com.helmy.coursesapp.Classes.CourseData
import com.helmy.coursesapp.Lecturer.Fragments.LecturerCoursesFragment
import com.helmy.coursesapp.R
import com.helmy.coursesapp.Student.Video2Student
import kotlinx.android.synthetic.main.courses_template.view.*
import kotlinx.android.synthetic.main.fragment_home.*


class AllCoursesFragment : Fragment(), TextWatcher {

    lateinit var const: Constants
    private var myAdapter: FirestoreRecyclerAdapter<CourseData, LecturerCoursesFragment.ViewH>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStart() {
        super.onStart()

        const = Constants(requireContext())
        getAllCourses()

        searchTxt.addTextChangedListener(this)

        myAdapter!!.startListening()
    }

    private fun getAllCourses() {

        val query = const.db.collection("Courses")

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
        CoursesRecycle.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = myAdapter
        }

    }

    private fun searchFor(text: String) {
        val array = ArrayList<CourseData>()

        const.db.collection("Courses").get().addOnSuccessListener { ar ->
            ar.forEach { co ->
                val course = co.toObject<CourseData>()

                if (course.CourseName.contains(text)) {
                    array.add(course)

                }
            }
        }

        CoursesRecycle.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL, false
            )
            adapter = adapterr(requireContext(), array)
        }
    }

    private fun gatAll() {
        val array = ArrayList<CourseData>()
        Thread {
            const.db.collection("Courses").get().addOnSuccessListener { ar ->
                ar.forEach { co ->
                    val course = co.toObject<CourseData>()
                    array.add(course)

                }
            }
        }.start()

        CoursesRecycle.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL, false
            )
            adapter = adapterr(requireContext(), array)
        }
    }

    class adapterr(val context: Context, val List: ArrayList<CourseData>) :
        RecyclerView.Adapter<adapterr.viewHolder>() {

        class viewHolder(I: View) : RecyclerView.ViewHolder(I)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {

            val II = LayoutInflater.from(context).inflate(R.layout.courses_template, parent, false)
            return viewHolder(II)
        }

        override fun onBindViewHolder(holder: viewHolder, pos: Int) {
            holder.itemView.name.text = List[pos].CourseName
            holder.itemView.num4videos.text = List[pos].NumberOfVideos.toString()
            holder.itemView.num4students.text = List[pos].NumberOfStudents.toString()

            if (List[pos].CourseImage.isNotEmpty())
            holder.itemView.image.load(List[pos].CourseImage)

            holder.itemView.setOnClickListener {
                val i = Intent(context, Video2Student::class.java)
                i.putExtra("CourseId", List[pos].CourseId)
                context.startActivity(i)
            }

        }

        override fun getItemCount(): Int {
            return List.size

        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(txt: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (txt!!.isNotEmpty()) {
            searchFor(txt.toString())
        } else {
            gatAll()
        }

    }

    override fun afterTextChanged(p0: Editable?) {

    }


}