package com.helmy.coursesapp.LecturerFragments.Videos

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import coil.load
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.activity_edit_video.*

class EditVideo : AppCompatActivity() {

    private var db = Firebase.firestore
    val storage = Firebase.storage.reference

    lateinit var progressDialog: ProgressDialog
    private var videoId = ""

    private var VideoUrl = ""
    private var VideoImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_video)

        progressDialog = ProgressDialog(this@EditVideo)
        progressDialog.apply {
            setTitle("Loading")
            setMessage("Loading")
            setCancelable(false)
        }

        videoId = intent.getStringExtra("VideoId").toString()

        getVideoData()

    }

    private fun getVideoData() {

        db.collection("Videos").whereEqualTo("VideoId",videoId).get().addOnSuccessListener {
            val video = it.documents[0].toObject<VideoData>()!!
            V_Name.setText(video.VideoName)
            V_desc.setText(video.VideoDesc)
            V_Num.setText(video.VideoNumber)
            V_Url.setText(video.VideoUrl)
            newImage.load(video.VideoImage)
        }

    }
}