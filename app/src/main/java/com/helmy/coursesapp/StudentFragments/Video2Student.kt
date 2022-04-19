package com.helmy.coursesapp.StudentFragments

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.helmy.coursesapp.Constants
import com.helmy.coursesapp.LecturerFragments.CoursesFragment
import com.helmy.coursesapp.LecturerFragments.Videos.ShowVideo
import com.helmy.coursesapp.LecturerFragments.Videos.VideoData
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.activity_video2_student.*
import kotlinx.android.synthetic.main.student_video_template.view.*

class Video2Student : AppCompatActivity() {

    lateinit var const: Constants
    var courseId = ""
    private var myAdapter: FirestoreRecyclerAdapter<VideoData, CoursesFragment.ViewH>? = null
    lateinit var resultLauncher:ActivityResultLauncher<Intent>
    var VideoId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video2_student)
        courseId = intent.getStringExtra("CourseId").toString()

        const = Constants(this)

        getAllData()

        resultLauncher =
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
                            uploadFile(
                                it.toString(),
                                const.auth.currentUser!!.email.toString(),
                                VideoId,
                                courseId
                            )
                            Toast.makeText(this, "UploadDone", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
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

    private fun getAllData() {

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
        myAdapter = object : FirestoreRecyclerAdapter<VideoData, CoursesFragment.ViewH>(option) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): CoursesFragment.ViewH {
                val i = LayoutInflater.from(this@Video2Student)
                    .inflate(R.layout.student_video_template, parent, false)
                return CoursesFragment.ViewH(i)
            }

            override fun onBindViewHolder(
                holder: CoursesFragment.ViewH,
                position: Int,
                model: VideoData
            ) {
                holder.itemView.name.text = model.VideoName
                VideoId = model.VideoId
                if (model.VideoImage.isNotEmpty()) {
                    holder.itemView.image.load(model.VideoImage)
                }
                holder.itemView.setOnClickListener {
                    val i = Intent(this@Video2Student, ShowVideo::class.java)
                    i.putExtra("VideoUrl", model.VideoUrl)
                    startActivity(i)
                }
                holder.itemView.quiz.setOnClickListener {
//                    val i = Intent(this@Video2Student, ShowVideo::class.java)
//                    i.putExtra("VideoUrl", model.VideoUrl)
//                    startActivity(i)
                }



                holder.itemView.uploadFile.setOnClickListener {
                    val intent = Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT)
                    resultLauncher.launch(Intent.createChooser(intent, "Select File"))
                }

                holder.itemView.downloadFile.setOnClickListener {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        requestPermissions(
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            1000
                        )
                    } else {
                        startDownloading(model.VideoFile)
                    }

                }

            }


        }
        customRecycle.apply {
            layoutManager =
                LinearLayoutManager(this@Video2Student, LinearLayoutManager.VERTICAL, false)
            adapter = myAdapter
        }


    }

    private fun uploadFile(
        fileUrl: String,
        studentEmail: String,
        videoId: String,
        courseId: String
    ) {

        val file = mapOf(
            "FileUrl" to fileUrl,
            "StudentEmail" to studentEmail,
            "VideoId" to videoId,
            "CourseId" to courseId,

            )
        const.db.collection("Tasks").add(file).addOnSuccessListener {
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startDownloading(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle("Download")
            .setDescription("the file is  downloading")
            .allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "${System.currentTimeMillis()}"
            )

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Click again to download ", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}