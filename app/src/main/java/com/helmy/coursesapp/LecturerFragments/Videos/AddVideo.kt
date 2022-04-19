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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.helmy.coursesapp.Constants
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.activity_add_video.*
import java.util.*

class AddVideo : AppCompatActivity() {

    private val const = Constants(this)

    private var VideoUrl = ""
    private var VideoImage = ""
    private var courseId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)

        courseId = intent.getStringExtra("CourseId").toString()

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    const.progressDialog.show()
                    // There are no request codes
                    val intent: Intent? = result.data
                    val uri = intent?.data  //The uri with the location of the file
                    val file = const.getFile(this, uri!!)
                    val new_uri = Uri.fromFile(file)

                    Toast.makeText(
                        this,
                        "${new_uri.lastPathSegment}",
                        Toast.LENGTH_SHORT
                    ).show()
                    val reference = const.storage.child("Videos/${new_uri.lastPathSegment}")
                    val uploadTask = reference.putFile(new_uri)

                    uploadTask.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            const.progressDialog.dismiss()
                            VideoUrl = it.toString()
                            Toast.makeText(this, "UploadDone", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        val resultLauncher2 =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    const.progressDialog.show()
                    // There are no request codes
                    val intent: Intent? = result.data
                    val uri = intent?.data  //The uri with the location of the file
                    val file = const.getFile(this, uri!!)
                    val new_uri = Uri.fromFile(file)

                    val reference = const.storage.child("Images/${new_uri.lastPathSegment}")
                    val uploadTask = reference.putFile(new_uri)

                    uploadTask.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            const.progressDialog.dismiss()
                            VideoImage = it.toString()
                            selectImage.load(VideoImage)
                            Toast.makeText(this, "UploadDone", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        btnn.setOnClickListener {
            newVideo()
        }

        selectVideo.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select Video"))
        }

        selectImage.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher2.launch(Intent.createChooser(intent, "Select Image"))
        }

    }

    private fun newVideo() {

        when {
            VideoName.text.toString().isEmpty() -> {
                Toast.makeText(this, "Name is Empty", Toast.LENGTH_SHORT).show()
            }
            VideoDesc.text.toString().isEmpty() -> {
                Toast.makeText(this, "Description is Empty", Toast.LENGTH_SHORT).show()
            }
            VideoNum.text.toString().isEmpty() -> {
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
                    "VideoId" to UUID.randomUUID().toString(),
                    "VideoName" to VideoName.text.toString(),
                    "VideoDesc" to VideoDesc.text.toString(),
                    "VideoNumber" to VideoNum.text.toString(),
                    "VideoUrl" to VideoUrl,
                    "VideoImage" to VideoImage,
                    "CourseId" to courseId
                )

                const.db.collection("Videos").add(video).addOnSuccessListener {
                    Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
                    const.db.collection("Courses").whereEqualTo("CourseId", courseId)
                        .get().addOnSuccessListener {documents ->
                            for(document in documents){
                                const.db.collection("Courses").document(document.id)
                                    .update("NumberOfVideos",(document.get("NumberOfVideos").toString().toLong()+1))
                            }
                    }
                    onBackPressed()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
                }

            }
        }


    }


}