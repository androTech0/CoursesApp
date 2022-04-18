package com.helmy.coursesapp.LecturerFragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.helmy.coursesapp.Constants
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.activity_add_video.*
import java.util.*

class AddVideo : AppCompatActivity() {

    private var db = Firebase.firestore
    val storage = Firebase.storage.reference

    lateinit var progressDialog: ProgressDialog
    private var VideoUrl = ""
    private var VideoImage = ""
    private var courseId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)


        progressDialog = ProgressDialog(this@AddVideo)
        progressDialog.apply {
            setTitle("Loading")
            setMessage("Loading")
            setCancelable(false)
        }

        courseId = intent.getStringExtra("CourseId").toString()

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    progressDialog.show()
                    // There are no request codes
                    val intent: Intent? = result.data
                    val uri = intent?.data  //The uri with the location of the file
                    val file = Constants().getFile(this, uri!!)
                    val new_uri = Uri.fromFile(file)

                    Toast.makeText(
                        this,
                        "${new_uri.lastPathSegment}",
                        Toast.LENGTH_SHORT
                    ).show()
                    val reference = storage.child("Videos/${new_uri.lastPathSegment}")
                    val uploadTask = reference.putFile(new_uri)

                    uploadTask.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            progressDialog.dismiss()
                            VideoUrl = it.toString()
                            Toast.makeText(this, "UploadDone", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(this, "UploadDone", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        btnn.setOnClickListener {
            if (VideoUrl.isNotEmpty() && VideoName.text.toString().isNotEmpty()) {
                newVideo()

            }
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
        val video = mapOf(
            "VideoId" to UUID.randomUUID().toString(),
            "VideoName" to VideoName.text.toString(),
            "VideoUrl" to VideoUrl,
            "VideoImage" to VideoImage,
            "CourseId" to courseId
        )
        db.collection("Videos").add(video).addOnSuccessListener {
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }.addOnFailureListener {
            Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
        }

    }



}