package com.helmy.coursesapp.LecturerFragments.Course

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.helmy.coursesapp.Constants
import com.helmy.coursesapp.LecturerFragments.Videos.AddVideo
import com.helmy.coursesapp.LecturerFragments.CoursesFragment
import com.helmy.coursesapp.LecturerFragments.Videos.ShowVideo
import com.helmy.coursesapp.LecturerFragments.Videos.VideoData
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.activity_course_content.*
import kotlinx.android.synthetic.main.video_template.*
import kotlinx.android.synthetic.main.video_template.view.*

class CourseContent : AppCompatActivity() {
    lateinit var const:Constants

    private var myAdapter: FirestoreRecyclerAdapter<VideoData, CoursesFragment.ViewH>? = null


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

        const.db.collection("Courses").whereEqualTo("CourseId", courseId).get().addOnSuccessListener {
            CourseName.text = it.documents[0].get("CourseName").toString()

            if (it.documents[0].get("CourseImage").toString().isNotEmpty()) {
                CourseImage.load(it.documents[0].get("CourseImage").toString())
            }
        }


        val query = const.db.collection("Videos").whereEqualTo("CourseId", courseId).orderBy("VideoNumber")
        val option =
            FirestoreRecyclerOptions.Builder<VideoData>().setQuery(query, VideoData::class.java)
                .build()
        myAdapter = object : FirestoreRecyclerAdapter<VideoData, CoursesFragment.ViewH>(option) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): CoursesFragment.ViewH {
                val i = LayoutInflater.from(this@CourseContent)
                    .inflate(R.layout.video_template, parent, false)
                return CoursesFragment.ViewH(i)
            }

            override fun onBindViewHolder(
                holder: CoursesFragment.ViewH,
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
                LinearLayoutManager(this@CourseContent, LinearLayoutManager.VERTICAL, false)
            adapter = myAdapter
        }


    }


}