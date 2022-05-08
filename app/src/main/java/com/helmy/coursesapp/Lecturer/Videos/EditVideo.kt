package com.helmy.coursesapp.Lecturer.Videos

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import coil.load
import com.google.firebase.firestore.ktx.toObject
import com.helmy.coursesapp.Classes.VideoData
import com.helmy.coursesapp.Classes.Constants
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.activity_edit_video.*

class EditVideo : AppCompatActivity() {

    lateinit var const: Constants

    private var videoId = ""
    private var newVideoUrl = ""
    private var VideoImage = ""
    private var VideoFile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_video)
        const = Constants(this)


        videoId = intent.getStringExtra("VideoId").toString()

        getVideoData()

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
                            newVideoUrl = it.toString()
                            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
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
                            newImage.load(VideoImage)
                            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
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
                            VideoFile = it.toString()
                            Toast.makeText(this, "UploadDone", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        V_Save.setOnClickListener {
            updateVideo()
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

        V_File.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher3.launch(Intent.createChooser(intent, "Select File"))
        }

    }

    private fun updateVideo() {
        when {
            V_Name.text.toString().isEmpty() -> {
                Toast.makeText(this, "Name is Empty", Toast.LENGTH_SHORT).show()
            }
            V_desc.text.toString().isEmpty() -> {
                Toast.makeText(this, "Description is Empty", Toast.LENGTH_SHORT).show()
            }
            newVideoUrl.isEmpty() -> {
                Toast.makeText(this, "VideoUrl is Empty", Toast.LENGTH_SHORT).show()
            }
            VideoImage.isEmpty() -> {
                Toast.makeText(this, "VideoImage is Empty", Toast.LENGTH_SHORT).show()
            }
            VideoFile.isEmpty() -> {
                Toast.makeText(this, "VideoFile is Empty", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val video = mapOf(
                    "VideoName" to V_Name.text.toString(),
                    "VideoDesc" to V_desc.text.toString(),
                    "VideoUrl" to newVideoUrl,
                    "VideoFile" to VideoFile,
                    "VideoImage" to VideoImage,
                )
                const.db.collection("Videos").whereEqualTo("VideoId", videoId).get()
                    .addOnSuccessListener {
                        const.db.collection("Videos").document(it.documents[0].id).update(video)
                            .addOnSuccessListener {
                                onBackPressed()
                                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                            }
                    }
            }
        }
    }

    private fun getVideoData() {

        const.db.collection("Videos").whereEqualTo("VideoId", videoId).get().addOnSuccessListener {
            val video = it.documents[0].toObject<VideoData>()!!
            V_Name.setText(video.VideoName)
            V_desc.setText(video.VideoDesc)
            newImage.load(video.VideoImage)

            V_Url.setOnClickListener {
                copyThis(video.VideoUrl)
            }

            newVideoUrl = video.VideoUrl
            VideoImage = video.VideoImage
            VideoFile = video.VideoFile



        }

    }


    fun copyThis(txt:String) {

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", txt)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()

    }



}