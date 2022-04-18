package com.helmy.coursesapp.LecturerFragments.Course

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import coil.load
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.helmy.coursesapp.Constants
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.activity_edit_course.*

class EditCourse : AppCompatActivity() {

    private val db = Firebase.firestore
    val storage = Firebase.storage.reference

    lateinit var progressDialog: ProgressDialog
    var imageUrl = ""
    private var CourseId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_course)

        progressDialog = ProgressDialog(this)
        progressDialog.apply {
            setTitle("Loading")
            setMessage("Loading")
            setCancelable(false)
        }

        CourseId = intent.getStringExtra("CourseId")!!

        getCourseData()

        val resultLauncher =
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
                    updateCourse(CourseName.text.toString(), imageUrl)
                }
            }
        }

        selectImage.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select image"))
        }


    }

    private fun getCourseData(){
        db.collection("Courses").whereEqualTo("CourseId",CourseId).get().addOnSuccessListener {
            CourseName.setText(it.documents[0].get("CourseName").toString())
            imageUrl = it.documents[0].get("CourseImage").toString()
            selectImage.load(imageUrl)
        }
    }

    private fun updateCourse(name: String, imageUrl: String) {

        val course = mapOf(
            "CourseName" to name,
            "CourseImage" to imageUrl
        )
        db.collection("Courses").whereEqualTo("CourseId",CourseId).get().addOnSuccessListener {
            db.collection("Courses").document(it.documents[0].id).update(course)
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }.addOnFailureListener {
            Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
        }
    }


}