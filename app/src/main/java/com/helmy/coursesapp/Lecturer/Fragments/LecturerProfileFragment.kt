package com.helmy.coursesapp.Lecturer.Fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import coil.load
import com.google.firebase.firestore.ktx.toObject
import com.helmy.coursesapp.Classes.Constants
import com.helmy.coursesapp.R
import com.helmy.coursesapp.Classes.UserData
import com.helmy.coursesapp.SendEmailActivity
import kotlinx.android.synthetic.main.fragment_profile2_lecturer.*
import java.util.*

class LecturerProfileFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    lateinit var const: Constants
    private var newImage = ""
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    const.progressDialog.show()
                    // There are no request codes
                    val intent: Intent? = result.data
                    val uri = intent?.data  //The uri with the location of the file
                    val file = const.getFile(requireContext(), uri!!)
                    val new_uri = Uri.fromFile(file)

                    val reference = const.storage.child("Images/${new_uri.lastPathSegment}")
                    val uploadTask = reference.putFile(new_uri)

                    uploadTask.addOnFailureListener { e ->
                    }.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            const.progressDialog.dismiss()
                            newImage = it.toString()
                            UserImage.load(newImage)
                        }
                    }
                }
            }

        return inflater.inflate(R.layout.fragment_profile2_lecturer, container, false)
    }

    override fun onStart() {
        super.onStart()

        const = Constants(requireContext())

        getUserInfo()



        UserImage.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select image"))
        }


        sendMail.setOnClickListener {
            startActivity(Intent(requireContext(),SendEmailActivity::class.java))
        }

        birthDate.setOnClickListener {
            val datePikerDialog = DatePickerDialog(
                requireContext(), this,
                2010,
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            datePikerDialog.show()
        }

        UpdateButton.setOnClickListener {
            updateUserInfo(
                NameEdit.text.toString(),
                birthDate.text.toString(),
                PhoneNumberEdit.text.toString()
            )
        }

        logOutBtn.setOnClickListener {
            Constants(requireContext()).logOut()
        }

    }

    private fun updateUserInfo(Name: String, Birthdate: String, Phone: String) {
        val email = const.auth.currentUser!!.email
        if (newImage.isNotEmpty()) {
            const.db.collection("Users").document(email.toString())
                .update(
                    "Name", Name,
                    "Image", newImage,
                    "Birthdate", Birthdate,
                    "Phone", Phone,
                ).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show()
                }
        } else {
            const.db.collection("Users").document(email.toString())
                .update(
                    "Name", Name,
                    "Birthdate", Birthdate,
                    "Phone", Phone,
                ).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun getUserInfo() {
        val email = const.auth.currentUser!!.email.toString()
        const.db.collection("Users").document(email).get().addOnSuccessListener { document ->
            if (document != null) {
                val userData = document.toObject<UserData>()
                NameEdit.setText(userData!!.Name)
                emailEdit.setText(userData.Email)
                PhoneNumberEdit.setText(userData.Phone)
                birthDate.text = userData.Birthdate
                UserImage.load(userData.Image)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        birthDate.text = "${p2 + 1}/$p3/$p1"
    }

}