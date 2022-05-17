package com.helmy.coursesapp.Student

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.FirebaseStorage
import com.helmy.coursesapp.Classes.Constants
import com.helmy.coursesapp.Lecturer.Fragments.LecturerCoursesFragment
import com.helmy.coursesapp.Lecturer.Videos.ShowVideo
import com.helmy.coursesapp.Classes.VideoData
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.activity_video2_student.*
import kotlinx.android.synthetic.main.activity_video2_student.CourseImage
import kotlinx.android.synthetic.main.activity_video2_student.CourseName
import kotlinx.android.synthetic.main.activity_video2_student.customRecycle
import kotlinx.android.synthetic.main.student_video_template.view.*

@Suppress("UNCHECKED_CAST")
class Video2Student : AppCompatActivity() {

    lateinit var const: Constants

    var courseId = ""
    var VideoId = ""
    var lecturerEmail = ""
    var regstered = false
    private var myAdapter: FirestoreRecyclerAdapter<VideoData, LecturerCoursesFragment.ViewH>? =
        null
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video2_student)

        courseId = intent.getStringExtra("CourseId").toString()

        const = Constants(this)

        checkJoin()
        getAllDataOf()

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
                                lecturerEmail
                            )
                        }
                    }
                }
            }

        join_btn.setOnClickListener {
            const.db.collection("Courses").whereEqualTo("CourseId", courseId)
                .get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        val user = const.auth.currentUser
                        val email = user!!.email
                        var exist = false
                        val dataRes = document.get("StudentsIDs").toString()
                            .substring(1, (document.get("StudentsIDs").toString().length - 1))
                        var ar = dataRes.split(",").map { it.trim() }
                        for (e in ar) {
                            if (e == email) {
                                exist = true
                            }
                        }
                        if (!exist) {
                            val next = arrayListOf<String>()
                            next.add(email.toString())
                            for (e in ar) {
                                if (e.isNotEmpty()) {
                                    next.add(e)
                                }
                            }
                            ar = next
                            const.db.collection("Courses").document(document.id)
                                .update(
                                    "StudentsIDs",
                                    ar
                                )
                            const.db.collection("Courses").document(document.id)
                                .update(
                                    "NumberOfStudents",
                                    (document.get("NumberOfStudents").toString().toLong() + 1)
                                )

                            const.db.collection("Users").document(email.toString())
                                .get().addOnSuccessListener {
                                    val oldHash: HashMap<String, HashMap<String, *>> =
                                        it.get("Courses") as HashMap<String, HashMap<String, *>>
                                    val newHash: HashMap<String, HashMap<String, *>> = hashMapOf(
                                        courseId to hashMapOf(
                                            "course_progress" to 0,
                                            "course_done" to false
                                        )
                                    )
                                    for (key in oldHash.keys) {
                                        newHash.put(key, oldHash[key]!!)
                                    }
                                    const.db.collection("Users").document(email.toString())
                                        .update(
                                            "Courses",
                                            newHash
                                        )
                                }
                            regstered = true
                            unjoin_btn.visibility = View.VISIBLE
                            join_btn.visibility = View.INVISIBLE
                        }

                    }
                }
        }

        unjoin_btn.setOnClickListener {
            const.db.collection("Courses").whereEqualTo("CourseId", courseId)
                .get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        val user = const.auth.currentUser
                        val email = user!!.email
                        val dataRes = document.get("StudentsIDs").toString()
                            .substring(1, (document.get("StudentsIDs").toString().length - 1))
                        var ar = dataRes.split(",").map { it.trim() }

                        val next = arrayListOf<String>()
                        for (e in ar) {
                            if (e.isNotEmpty() && e != email) {
                                next.add(e)
                            }
                        }
                        ar = next
                        const.db.collection("Courses").document(document.id)
                            .update(
                                "StudentsIDs",
                                ar
                            )
                        const.db.collection("Courses").document(document.id)
                            .update(
                                "NumberOfStudents",
                                (document.get("NumberOfStudents").toString().toLong() - 1)
                            )
                        const.db.collection("Users").document(email.toString())
                            .get().addOnSuccessListener {
                                val oldHash: HashMap<String, HashMap<String, *>> =
                                    it.get("Courses") as HashMap<String, HashMap<String, *>>
                                val newHash: HashMap<String, HashMap<String, *>> = hashMapOf()
                                for (key in oldHash.keys) {
                                    if (key != courseId) {
                                        newHash.put(key, oldHash[key]!!)
                                    }

                                }
                                const.db.collection("Users").document(email.toString())
                                    .update(
                                        "Courses",
                                        newHash
                                    )
                            }
                        regstered = false
                        unjoin_btn.visibility = View.INVISIBLE
                        join_btn.visibility = View.VISIBLE
                    }
                }
        }

    }

    private fun getAllDataOf() {

        const.db.collection("Courses").whereEqualTo("CourseId", courseId).get()
            .addOnSuccessListener {

                CourseName.text = it.documents[0].get("CourseName").toString()
                lecturerEmail = it.documents[0].get("LecturerEmail").toString()
                if (it.documents[0].get("CourseImage").toString().isNotEmpty()) {
                    CourseImage.load(it.documents[0].get("CourseImage").toString())
                }
            }


        val query =
            const.db.collection("Videos").whereEqualTo("CourseId", courseId).orderBy("VideoNumber")
        val option =
            FirestoreRecyclerOptions.Builder<VideoData>().setQuery(query, VideoData::class.java)
                .build()
        myAdapter =
            object : FirestoreRecyclerAdapter<VideoData, LecturerCoursesFragment.ViewH>(option) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): LecturerCoursesFragment.ViewH {
                    val i = LayoutInflater.from(this@Video2Student)
                        .inflate(R.layout.student_video_template, parent, false)
                    return LecturerCoursesFragment.ViewH(i)
                }

                override fun onBindViewHolder(
                    holder: LecturerCoursesFragment.ViewH,
                    position: Int,
                    model: VideoData
                ) {

                    holder.itemView.name.text = model.VideoName

                    if (model.VideoImage.isNotEmpty()) {
                        holder.itemView.image.load(model.VideoImage)
                    }

                    holder.itemView.desc.setOnClickListener {
                        val dialog = Dialog(this@Video2Student)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(false)
                        dialog.setContentView(R.layout.description_video_dialog)

                        val viewText = dialog.findViewById<TextView>(R.id.desc_view)

                        val closeButton = dialog.findViewById<Button>(R.id.close_btn)

                        viewText.text = model.VideoDesc

                        closeButton.setOnClickListener {
                            dialog.dismiss()
                        }

                        dialog.show()
                    }

                    holder.itemView.setOnClickListener {

                        if (regstered) {

                            val email = const.auth.currentUser!!.email

                            const.db.collection("Users").document(email.toString())
                                .get().addOnSuccessListener { itt ->
                                    val mmm: HashMap<String, HashMap<String, *>> =
                                        itt.get("Courses") as HashMap<String, HashMap<String, *>>
                                    val m = mmm[courseId]
                                    val progress = m!!["course_progress"].toString().toLong()

                                    if (model.VideoNumber <= progress ||
                                        model.VideoNumber == progress + 1
                                    ) {
                                        const.db.collection("Videos")
                                            .whereEqualTo("VideoId", model.VideoId)
                                            .get().addOnSuccessListener {
                                                val vidNum =
                                                    it.documents[0].get("VideoNumber").toString()
                                                        .toLong()

                                                const.db.collection("Users")
                                                    .document(email.toString())
                                                    .get().addOnSuccessListener { ittt ->
                                                        val oldHash: HashMap<String, HashMap<String, *>> =
                                                            ittt.get("Courses") as HashMap<String, HashMap<String, *>>
                                                        val newHash: HashMap<String, HashMap<String, *>> =
                                                            hashMapOf()
                                                        val nId = oldHash[courseId]

                                                        if (nId!!["course_progress"].toString()
                                                                .toLong() < model.VideoNumber.toLong()
                                                        ) {
                                                            for (key in oldHash.keys) {
                                                                if (key == courseId) {
                                                                    newHash.put(
                                                                        key, hashMapOf(
                                                                            "course_progress" to vidNum,
                                                                            "course_done" to false
                                                                        )
                                                                    )
                                                                    continue
                                                                } else {
                                                                    newHash.put(
                                                                        key,
                                                                        oldHash[key]!!
                                                                    )
                                                                }
                                                            }
                                                            const.db.collection("Users")
                                                                .document(email.toString())
                                                                .update(
                                                                    "Courses",
                                                                    newHash
                                                                )
                                                        }
                                                    }
                                            }
                                        val i =
                                            Intent(this@Video2Student, ShowVideo::class.java)
                                        i.putExtra("VideoUrl", model.VideoUrl)
                                        startActivity(i)
                                    } else {
                                        Toast.makeText(
                                            this@Video2Student,
                                            "you didn't complete last video",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                }

                        } else {
                            Toast.makeText(
                                this@Video2Student,
                                "not regestered yet",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                    holder.itemView.uploadFile.setOnClickListener {
                        if (regstered) {
                            VideoId = model.VideoId
                            val intent = Intent()
                                .setType("*/*")
                                .setAction(Intent.ACTION_GET_CONTENT)
                            resultLauncher.launch(Intent.createChooser(intent, "Select File"))

                        } else {
                            Toast.makeText(
                                this@Video2Student,
                                "not regestered yet",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                    holder.itemView.downloadFile.setOnClickListener {
                        if (regstered) {
                            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                requestPermissions(
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    1000
                                )
                            } else {
                                val storageRef =
                                    FirebaseStorage.getInstance().reference.child(model.VideoFile)
                                storageRef.downloadUrl.addOnSuccessListener {
                                    Toast.makeText(
                                        this@Video2Student,
                                        it.toString(),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    startDownloading(it.toString(), model.VideoFile)
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@Video2Student,
                                "not regestered yet",
                                Toast.LENGTH_SHORT
                            ).show()
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

    private fun checkJoin() {
        const.db.collection("Courses").whereEqualTo("CourseId", courseId)
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = const.auth.currentUser
                    val email = user!!.email
                    val dataRes = document.get("StudentsIDs").toString()
                        .substring(1, (document.get("StudentsIDs").toString().length - 1))
                    val ar = dataRes.split(",").map { it.trim() }
                    for (e in ar) {
                        if (e == email) {
                            regstered = true
                        }
                    }
                    if (!regstered) {
                        join_btn.visibility = View.VISIBLE
                    } else {
                        unjoin_btn.visibility = View.VISIBLE
                    }
                }
            }
    }

    private fun uploadFile(
        fileUrl: String,
        studentEmail: String,
        videoId: String,
        lecturerEmail: String
    ) {

        val file = mapOf(
            "FileUrl" to fileUrl,
            "StudentEmail" to studentEmail,
            "VideoId" to videoId,
            "LecturerEmail" to lecturerEmail,

            )
        const.db.collection("Tasks").add(file).addOnSuccessListener {
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
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

    override fun onStart() {
        super.onStart()
        myAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        myAdapter!!.stopListening()
    }

}