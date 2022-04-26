package com.helmy.coursesapp.Lecturer.Fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.helmy.coursesapp.Constants
import com.helmy.coursesapp.Lecturer.Course.CourseData
import com.helmy.coursesapp.Lecturer.Course.EditCourse
import com.helmy.coursesapp.Lecturer.ViewVideos4Edit
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.courses_template.view.*
import kotlinx.android.synthetic.main.fragment_edit.*

class EditFragment : Fragment() {


    private var myAdapter: FirestoreRecyclerAdapter<CourseData, ViewH>? = null

    override fun onStart() {
        super.onStart()

        getAllCourses()

        myAdapter!!.startListening()

    }

    private fun getAllCourses() {
        val const = Constants(requireActivity())

        val query =
            const.db.collection("Courses").whereEqualTo("LecturerEmail", const.auth.currentUser!!.email)
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
                holder.itemView.editBtn.visibility = View.VISIBLE


                holder.itemView.editBtn.setOnClickListener {
                    val pop = PopupMenu(this@EditFragment.requireContext(), holder.itemView.editBtn)
                    pop.menuInflater.inflate(R.menu.edit_course_menu, pop.menu)
                    pop.setOnMenuItemClickListener { x ->
                        when (x.itemId) {
                            R.id.EditCourse -> {
                                val i = Intent(requireContext(), EditCourse::class.java)
                                i.putExtra("CourseId", model.CourseId)
                                startActivity(i)
                            }

                            R.id.DeleteCourse -> {

                                val dialog = AlertDialog.Builder(this@EditFragment.requireContext())
                                dialog.apply {
                                    setTitle("warning")
                                    setMessage("Will delete all videos ")
                                    setPositiveButton("Ok") { _, _ ->
                                        const.db.collection("Courses")
                                            .whereEqualTo("CourseId", model.CourseId)
                                            .get()
                                            .addOnSuccessListener {
                                                const.db.collection("Courses")
                                                    .document(it.documents[0].id)
                                                    .delete()
                                                    .addOnSuccessListener {
                                                        const.db.collection("Videos")
                                                            .whereEqualTo(
                                                                "CourseId",
                                                                model.CourseId
                                                            ).get()
                                                            .addOnSuccessListener { videos ->
                                                                if (videos.size() > 0) {
                                                                    for (i in 0 until videos.size()) {
                                                                        const.db.collection("Videos")
                                                                            .document(videos.documents[i].id)
                                                                            .delete()
                                                                            .addOnSuccessListener {
                                                                                Toast.makeText(
                                                                                    this@EditFragment.requireContext(),
                                                                                    "$i deleted",
                                                                                    Toast.LENGTH_SHORT
                                                                                )
                                                                                    .show()
                                                                            }

                                                                    }
                                                                }
                                                            }
                                                    }
                                            }
                                    }
                                    setNegativeButton("Cancel") { d, _ ->
                                        d.cancel()
                                    }
                                    create().show()
                                }


                            }

                            R.id.HideCourse -> {

                            }
                        }
                        true
                    }
                    pop.show()
                }

                holder.itemView.setOnClickListener {
                    val i = Intent(requireContext(), ViewVideos4Edit::class.java)
                    i.putExtra("CourseId", model.CourseId)
                    startActivity(i)
                }
            }
        }
        editRecycle.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = myAdapter
        }

    }

    override fun onStop() {
        super.onStop()
        myAdapter!!.stopListening()
    }
    class ViewH(i: View) : RecyclerView.ViewHolder(i)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

}