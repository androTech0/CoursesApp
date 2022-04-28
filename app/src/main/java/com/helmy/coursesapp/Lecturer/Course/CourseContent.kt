package com.helmy.coursesapp.Lecturer.Course

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.helmy.coursesapp.Constants
import com.helmy.coursesapp.Lecturer.Videos.AddVideo
import com.helmy.coursesapp.Lecturer.Fragments.LecturerCoursesFragment
import com.helmy.coursesapp.Lecturer.Videos.ShowVideo
import com.helmy.coursesapp.Classes.VideoData
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.activity_course_content.*
import kotlinx.android.synthetic.main.student_video_template.*
import kotlinx.android.synthetic.main.video_template.*
import kotlinx.android.synthetic.main.video_template.view.*

class CourseContent : AppCompatActivity() {
    lateinit var const: Constants

    private var myAdapter: FirestoreRecyclerAdapter<VideoData, LecturerCoursesFragment.ViewH>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_content)
        const = Constants(this)


        val courseId = intent.getStringExtra("CourseId").toString()

        getAllDataOf(courseId)

        addVideoBtn.setOnClickListener {
            val i = Intent(this, AddVideo::class.java)
            i.putExtra("CourseId", courseId)
            startActivity(i)
        }

        showStudents.setOnClickListener {
            val i = Intent(this, CourseUsers::class.java)
            i.putExtra("CourseId", courseId)
            startActivity(i)
        }

    }

    override fun onStart() {
        super.onStart()
        myAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        myAdapter!!.stopListening()
    }

    private fun getAllDataOf(courseId: String) {

        const.db.collection("Courses").whereEqualTo("CourseId", courseId).get()
            .addOnSuccessListener {
                CourseName.text = it.documents[0].get("CourseName").toString()

                if (it.documents[0].get("CourseImage").toString().isNotEmpty()) {
                    CourseImage.load(it.documents[0].get("CourseImage").toString())
                }
            }


        val query = const.db.collection("Videos").whereEqualTo("CourseId", courseId)
        val option =
            FirestoreRecyclerOptions.Builder<VideoData>().setQuery(query, VideoData::class.java)
                .build()
        myAdapter = object : FirestoreRecyclerAdapter<VideoData, LecturerCoursesFragment.ViewH>(option) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): LecturerCoursesFragment.ViewH {
                val i = LayoutInflater.from(this@CourseContent)
                    .inflate(R.layout.video_template, parent, false)
                return LecturerCoursesFragment.ViewH(i)
            }

            override fun onBindViewHolder(
                holder: LecturerCoursesFragment.ViewH,
                position: Int,
                model: VideoData
            ) {
                holder.itemView.name.text = model.VideoName

                if (model.VideoImage.isNotEmpty()) {
                    holder.itemView.image.load(model.VideoImage)
                }
                holder.itemView.setOnClickListener {
                    val i = Intent(this@CourseContent, ShowVideo::class.java)
                    i.putExtra("VideoUrl", model.VideoUrl)
                    startActivity(i)
                }


            }
        }
        customRecycle.apply {
            layoutManager =
                GridLayoutManager(context,2)
            adapter = myAdapter
        }


    }


}