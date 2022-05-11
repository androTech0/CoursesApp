package com.helmy.coursesapp.Lecturer.Videos

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import coil.load
import com.helmy.coursesapp.Classes.Constants
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.activity_add_video.*
import java.util.*

class AddVideo : AppCompatActivity() {

    lateinit var const: Constants
    private var courseId = ""

    private var videoFile = ""
    private var videoUrl = ""
    private var videoImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)
        const = Constants(this)

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

                    val reference = const.storage.child("Videos/${new_uri.lastPathSegment}")
                    val uploadTask = reference.putFile(new_uri)

                    uploadTask.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            const.progressDialog.dismiss()
                            videoUrl = it.toString()
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
                            videoImage = it.toString()
                            selectImage.load(videoImage)
                            Toast.makeText(this, "UploadDone", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        val resultLauncher3 =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    const.progressDialog.show()
                    // There are no request codes
                    val intent: Intent? = result.data
                    val uri = intent?.data  //The uri with the location of the file
                    val file = const.getFile(this, uri!!)
                    val new_uri = Uri.fromFile(file)

                    val reference = const.storage.child("Files/${new_uri.lastPathSegment}")
                    val uploadTask = reference.putFile(new_uri)
                    uploadTask.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            const.progressDialog.dismiss()
                            videoFile = "Files/${new_uri.lastPathSegment}"
                            Toast.makeText(this, "UploadDone", Toast.LENGTH_SHORT).show()
                            Toast.makeText(this, "${new_uri.lastPathSegment}", Toast.LENGTH_SHORT).show()
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

        btnFile.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher3.launch(Intent.createChooser(intent, "Select File"))
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
            videoUrl.isEmpty() -> {
                Toast.makeText(this, "VideoUrl is Empty", Toast.LENGTH_SHORT).show()
            }
            videoImage.isEmpty() -> {
                Toast.makeText(this, "VideoImage is Empty", Toast.LENGTH_SHORT).show()
            }
            videoFile.isEmpty() -> {
                Toast.makeText(this, "VideoFile is Empty", Toast.LENGTH_SHORT).show()
            }
            else -> {

                const.db.collection("Courses").whereEqualTo("CourseId", courseId).get().addOnSuccessListener {
                    val videoNumber =
                        it.documents[0].get("NumberOfVideos").toString().toLong() + 1


                    val video = mapOf(
                        "VideoId" to UUID.randomUUID().toString(),
                        "VideoName" to VideoName.text.toString(),
                        "VideoDesc" to VideoDesc.text.toString(),
                        "VideoNumber" to videoNumber,
                        "VideoUrl" to videoUrl,
                        "VideoImage" to videoImage,
                        "VideoFile" to videoFile,
                        "CourseId" to courseId
                    )

                    const.db.collection("Videos").add(video).addOnSuccessListener {
                        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
                        const.db.collection("Courses").whereEqualTo("CourseId", courseId)
                            .get().addOnSuccessListener { documents ->
                                for (document in documents) {
                                    const.db.collection("Courses").document(document.id)
                                        .update(
                                            "NumberOfVideos",
                                            (document.get("NumberOfVideos").toString().toLong() + 1)
                                        )
                                }
                            }
                        onBackPressed()
                    }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
                        }
                }

            }
        }


    }


}