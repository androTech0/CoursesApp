package com.helmy.coursesapp.LecturerFragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.helmy.coursesapp.Constants
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.fragment_add.*
import java.util.*


class AddCourse : Fragment() {

    private var db = Firebase.firestore
    val storage = Firebase.storage.reference

    lateinit var progressDialog: ProgressDialog
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    var path = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    progressDialog.show()
                    // There are no request codes
                    val intent: Intent? = result.data
                    val uri = intent?.data  //The uri with the location of the file
                    val file = Constants().getFile(requireContext(), uri!!)
                    val new_uri = Uri.fromFile(file)

                    Toast.makeText(
                        requireContext(),
                        "${new_uri.lastPathSegment}",
                        Toast.LENGTH_SHORT
                    ).show()
                    val reference = storage.child("Images/${new_uri.lastPathSegment}")
                    val uploadTask = reference.putFile(new_uri)

                    uploadTask.addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            progressDialog.dismiss()
                            path = it.toString()
                            Toast.makeText(requireContext(), "Done", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }

    override fun onStart() {
        super.onStart()


        progressDialog = ProgressDialog(requireContext())
        progressDialog.apply {
            setTitle("Loading")
            setMessage("Loading")
            setCancelable(false)
        }




        btn.setOnClickListener {
            if (CourseName.text.toString().isNotEmpty() && path.isNotEmpty()) {
                newCourse(CourseName.text.toString(), path)
            }
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
            "CourseImage" to image
        )
        db.collection("Courses").add(course).addOnSuccessListener {
            Toast.makeText(requireContext(), "Added", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failure", Toast.LENGTH_SHORT).show()
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_add, container, false)
    }

}