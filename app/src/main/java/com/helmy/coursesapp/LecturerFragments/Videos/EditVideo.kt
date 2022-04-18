package com.helmy.coursesapp.LecturerFragments.Videos

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import coil.load
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.helmy.coursesapp.Constants
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

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    progressDialog.show()
                    // There are no request codes
                    val intent: Intent? = result.data
                    val uri = intent?.data  //The uri with the location of the file
                    val file = Constants().getFile(this, uri!!)
                    val new_uri = Uri.fromFile(file)

                    val reference = storage.child("Videos/${new_uri.lastPathSegment}")
                    val uploadTask = reference.putFile(new_uri)

                    uploadTask.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            progressDialog.dismiss()
                            VideoUrl = it.toString()
                            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        val resultLauncher2 =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    progressDialog.show()
                    // There are no request codes
                    val intent: Intent? = result.data
                    val uri = intent?.data  //The uri with the location of the file
                    val file = Constants().getFile(this, uri!!)
                    val new_uri = Uri.fromFile(file)

                    val reference = storage.child("Images/${new_uri.lastPathSegment}")
                    val uploadTask = reference.putFile(new_uri)

                    uploadTask.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            progressDialog.dismiss()
                            VideoImage = it.toString()
                            newImage.load(VideoImage)
                            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        V_Save.setOnClickListener {

            when {
                V_Name.text.toString().isEmpty() -> {
                    Toast.makeText(this, "Name is Empty", Toast.LENGTH_SHORT).show()
                }
                V_desc.text.toString().isEmpty() -> {
                    Toast.makeText(this, "Description is Empty", Toast.LENGTH_SHORT).show()
                }
                V_Num.text.toString().isEmpty() -> {
                    Toast.makeText(this, "number is Empty", Toast.LENGTH_SHORT).show()
                }
                VideoUrl.isEmpty() -> {
                    Toast.makeText(this, "VideoUrl is Empty", Toast.LENGTH_SHORT).show()
                }
                VideoImage.isEmpty() -> {
                    Toast.makeText(this, "VideoImage is Empty", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val video = mapOf(
                        "VideoName" to V_Name.text.toString(),
                        "VideoDesc" to V_desc.text.toString(),
                        "VideoNumber" to V_Num.text.toString(),
                        "VideoUrl" to VideoUrl,
                        "VideoImage" to VideoImage,
                    )
                    db.collection("Videos").whereEqualTo("VideoId", videoId).get().addOnSuccessListener {
                        db.collection("Videos").document(it.documents[0].id).update(video).addOnSuccessListener {
                            onBackPressed()
                            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        newVideo.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select Video"))
        }

        newImage.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher2.launch(Intent.createChooser(intent, "Select image"))
        }

    }

    private fun getVideoData() {

        db.collection("Videos").whereEqualTo("VideoId", videoId).get().addOnSuccessListener {
            val video = it.documents[0].toObject<VideoData>()!!
            V_Name.setText(video.VideoName)
            V_desc.setText(video.VideoDesc)
            V_Num.setText(video.VideoNumber)
            V_Url.text = video.VideoUrl
            newImage.load(video.VideoImage)

            VideoUrl = video.VideoUrl
            VideoImage = video.VideoImage
        }

    }
}