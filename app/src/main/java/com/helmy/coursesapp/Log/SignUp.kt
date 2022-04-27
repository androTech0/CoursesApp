package com.helmy.coursesapp.Log

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.helmy.coursesapp.Constants
import com.helmy.coursesapp.Lecturer.LecturerMainActivity
import com.helmy.coursesapp.R
import com.helmy.coursesapp.Student.StudentMainActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUp : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private var userImage = ""
    lateinit var const:Constants
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        const = Constants(this)

       val resultLauncher =
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
                            userImage = it.toString()
                            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }


        loginGo.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }

          birth.setOnClickListener {
              val datePikerDialog = DatePickerDialog(
                  this, this,
                  2010,
                  Calendar.getInstance().get(Calendar.MONTH),
                  Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
              )
              datePikerDialog.show()
          }


        image.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select image"))
        }


        SignupButton.setOnClickListener {

            if (E_Name.text!!.isNotEmpty() &&
                E_Email.text!!.isNotEmpty() &&
                E_Password.text!!.isNotEmpty() &&
                birth.text.isNotEmpty() &&
                E_Phone.text!!.isNotEmpty()
            ) {
                if (userImage.isNotEmpty()) {
                    if (E_Email.text.toString().contains("@")) {
                        if (E_Password.text.toString().length > 6) {
                            val choice = radioGroup.checkedRadioButtonId
                            val radioButton = findViewById<RadioButton>(choice)
                            saveUserData(
                                E_Name.text.toString(),
                                E_Email.text.toString(),
                                E_Password.text.toString(),
                                E_Phone.text.toString(),
                                birth.text.toString(),
                                radioButton.text.toString()
                            )
                        } else
                            Toast.makeText(this, "Phone must be at least 7", Toast.LENGTH_SHORT)
                                .show()
                    } else
                        Toast.makeText(this, "email not contain @ character !!", Toast.LENGTH_SHORT)
                            .show()
                }else{
                    Toast.makeText(this, "Upload Image", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun saveUserData(
        name: String, email: String, password: String, Phone_number: String,birth:String ,kind: String
    ) {

        val const = Constants(this)
        const.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = hashMapOf(
                        "Name" to name,
                        "Email" to email,
                        "Phone" to Phone_number,
                        "Password" to password,
                        "Kind" to kind,
                        "Image" to userImage,
                        "Birthdate" to birth,
                        "Courses" to hashMapOf<String, HashMap<String, *>>()
                    )

                    val shared = getSharedPreferences("shared", MODE_PRIVATE).edit()
                    const.db.collection("Users")
                        .document(email)
                        .set(user)
                        .addOnSuccessListener {
                            if (kind == getString(R.string.lecturer)) {
                                Toast.makeText(
                                    baseContext,
                                    "Sign up Successful.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                shared.putString("kind", getString(R.string.lecturer)).apply()
                                startActivity(Intent(this, LecturerMainActivity::class.java))
                                finish()
                            } else if (kind == getString(R.string.student)) {
                                Toast.makeText(
                                    baseContext,
                                    "Sign up Successful.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                shared.putString("kind", getString(R.string.student)).apply()
                                startActivity(Intent(this, StudentMainActivity::class.java))
                                finish()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                baseContext,
                                "Store user data failed.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                } else {
                    Toast.makeText(baseContext, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        birth.text = "${p2 + 1}/$p3/$p1"
    }
}