package com.helmy.coursesapp.Lecturer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.helmy.coursesapp.Constants
import com.helmy.coursesapp.Lecturer.Fragments.CoursesFragment
import com.helmy.coursesapp.Lecturer.Videos.EditVideo
import com.helmy.coursesapp.Lecturer.Videos.VideoData
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.video_template.view.*
import kotlinx.android.synthetic.main.view_videos_to_edit.*

class ViewVideos4Edit : AppCompatActivity() {


    private var myAdapter: FirestoreRecyclerAdapter<VideoData, CoursesFragment.ViewH>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_videos_to_edit)

        val courseId = intent.getStringExtra("CourseId")!!

        getAllDataOf(courseId)

    }

    private fun getAllDataOf(courseId: String) {

        val const = Constants(this)

        const.db.collection("Courses").whereEqualTo("CourseId", courseId).get()
            .addOnSuccessListener {
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
                val i = LayoutInflater.from(this@ViewVideos4Edit)
                    .inflate(R.layout.video_template, parent, false)
                return CoursesFragment.ViewH(i)
            }

            override fun onBindViewHolder(
                holder: CoursesFragment.ViewH,
                position: Int,
                model: VideoData
            ) {


                holder.itemView.name.text = model.VideoName
                holder.itemView.DeleteVideo.visibility = View.VISIBLE
                holder.itemView.DeleteVideo.setOnClickListener {
                    const.db.collection("Videos").whereEqualTo("VideoId", model.VideoId).get()
                        .addOnSuccessListener {

                            val d = AlertDialog.Builder(this@ViewVideos4Edit)
                            d.setTitle("Delete Video")
                            d.setMessage(" do you wanna delete this Video !?!")
                            d.setPositiveButton("Delete") { _, _ ->
                                Toast.makeText(this@ViewVideos4Edit, "Deleted", Toast.LENGTH_SHORT)
                                    .show()
                                const.db.collection("Videos").document(it.documents[0].id).delete()
                                const.db.collection("Courses").whereEqualTo("CourseId", courseId)
                                    .get().addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            const.db.collection("Courses").document(document.id)
                                                .update("NumberOfVideos",
                                                    (document.get("NumberOfVideos").toString()
                                                        .toLong() - 1)
                                                )
                                        }
                                    }
                            }.setNegativeButton("cancel") { _d, _ ->
                                _d.dismiss()
                            }.create().show()

                        }
                }

                if (model.VideoImage.isNotEmpty()) {
                    holder.itemView.image.load(model.VideoImage)
                }

                holder.itemView.setOnClickListener {
                    val i = Intent(this@ViewVideos4Edit, EditVideo::class.java)
                    i.putExtra("VideoId", model.VideoId)
                    startActivity(i)
                }


            }
        }
        editRecycle.apply {
            layoutManager =
                LinearLayoutManager(this@ViewVideos4Edit, LinearLayoutManager.VERTICAL, false)
            adapter = myAdapter
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


}

