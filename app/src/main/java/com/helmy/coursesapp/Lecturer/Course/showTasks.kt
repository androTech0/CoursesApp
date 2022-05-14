package com.helmy.coursesapp.Lecturer.Course

import android.Manifest
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.helmy.coursesapp.Classes.Constants
import com.helmy.coursesapp.Classes.TasksClass
import com.helmy.coursesapp.Lecturer.Fragments.LecturerCoursesFragment
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.activity_show_tasks.*
import kotlinx.android.synthetic.main.users_tasks_template.*
import kotlinx.android.synthetic.main.users_tasks_template.view.*

class showTasks : AppCompatActivity() {

    lateinit var const: Constants
    private var myAdapter: FirestoreRecyclerAdapter<TasksClass, LecturerCoursesFragment.ViewH>? =
        null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_tasks)
        const = Constants(this)
        val videoId = intent.getStringExtra("VideoId").toString()

        getAllDataOf(videoId)

    }

    override fun onStart() {
        super.onStart()
        myAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        myAdapter!!.stopListening()
    }

    private fun getAllDataOf(videoId: String) {
        val currentEmail = const.auth.currentUser!!.email.toString()
        val query = const.db.collection("Tasks").whereEqualTo("VideoId", videoId)
            .whereEqualTo("LecturerEmail", currentEmail)
        val option =
            FirestoreRecyclerOptions.Builder<TasksClass>().setQuery(query, TasksClass::class.java)
                .build()
        myAdapter =
            object : FirestoreRecyclerAdapter<TasksClass, LecturerCoursesFragment.ViewH>(option) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): LecturerCoursesFragment.ViewH {
                    val i = LayoutInflater.from(this@showTasks)
                        .inflate(R.layout.users_tasks_template, parent, false)
                    return LecturerCoursesFragment.ViewH(i)
                }

                override fun onBindViewHolder(
                    holder: LecturerCoursesFragment.ViewH,
                    position: Int,
                    model: TasksClass
                ) {

                    const.db.collection("Users").document(model.StudentEmail).get()
                        .addOnSuccessListener {
                            holder.itemView.U_Name.text = it.get("Name").toString()

                            if (it.get("Image").toString().isNotEmpty()) {
                                U_Image.load(it.get("Image").toString())
                            }
                        }

                    holder.itemView.U_File.setOnClickListener {

                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                            requestPermissions(
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                1000
                            )
                        } else {
                            startDownloading(model.FileUrl, "Task File")
                        }
                    }


                }
            }
        tasksRecycle.apply {
            layoutManager =
                LinearLayoutManager(this@showTasks)
            adapter = myAdapter
        }


    }

    private fun startDownloading(url: String, name: String) {
        Toast.makeText(this, url, Toast.LENGTH_SHORT).show()
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle(name)
            .setDescription("the file is  downloading")
            .allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                name
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