package com.helmy.coursesapp.Lecturer.Course

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.helmy.coursesapp.Classes.Constants
import com.helmy.coursesapp.Notify.PushNotification
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.add_course.*
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log
import com.helmy.coursesapp.Notify.NotificationData
import com.helmy.coursesapp.Notify.RetrofitInstance


const val TOPIC = "/topics/myTopic2"

class AddCourse : AppCompatActivity() {

    lateinit var const: Constants
    val TAG = "AddCourse"

    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    var imageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_course)

        const = Constants(this)

        resultLauncher =
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
                            imageUrl = it.toString()
                            selectImage.load(imageUrl)
                            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }


        btn.setOnClickListener {
            when {
                CourseName.text.toString().isEmpty() -> {
                    Toast.makeText(this, "name is empty", Toast.LENGTH_SHORT).show()
                }
                imageUrl.isEmpty() -> {
                    Toast.makeText(this, "image is empty", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    newCourse(CourseName.text.toString(), imageUrl)
                }
            }
        }

        backBtn.setOnClickListener {
            onBackPressed()
        }

        selectImage.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select image"))
        }


    }

    private fun newCourse(name: String, image: String) {
        val course = mapOf(
            "CourseId" to UUID.randomUUID().toString(),
            "CourseName" to name,
            "CourseImage" to image,
            "LecturerEmail" to const.auth.currentUser!!.email,
            "NumberOfVideos" to 0,
            "NumberOfStudents" to 0,
            "StudentsIDs" to listOf<String>()
        )
        const.db.collection("Courses").add(course).addOnSuccessListener {
            Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
            //--------------
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
            Handler().postDelayed({
                PushNotification(
                    NotificationData("Courses App", "${course.get("CourseName").toString()} course has added !!"), TOPIC
                ).also {
                    sendNotification(it)
                }
            },1500)
            //--------------
            onBackPressed()
        }.addOnFailureListener {
            Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
        }

    }



    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

}