package com.helmy.coursesapp.Log

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.Toast
import com.helmy.coursesapp.Constants
import com.helmy.coursesapp.Lecturer.LecturerMainActivity
import com.helmy.coursesapp.R
import com.helmy.coursesapp.Student.StudentMainActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUp : AppCompatActivity(), DatePickerDialog.OnDateSetListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        loginGo.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        BirthdayNameLabel.setOnClickListener {
            val datePikerDialog = DatePickerDialog(
                this, this,
                2010,
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            datePikerDialog.show()
        }

        SignupButton.setOnClickListener {

            if (FirstNameEdit.text.isNotEmpty() &&
                MiddleNameEdit.text.isNotEmpty() &&
                LastNameEdit.text.isNotEmpty() &&
                emailEdit.text.isNotEmpty() &&
                PhoneNumberEdit.text.isNotEmpty() &&
                PasswordEdit.text.isNotEmpty() &&
                confirmPasswordEdit.text.isNotEmpty() &&
                BirthdayEdit.text.isNotEmpty()
            ) {
                if (confirmPasswordEdit.text.toString() == PasswordEdit.text.toString()) {
                    if (emailEdit.text.toString().contains("@")) {
                        if (PhoneNumberEdit.text.toString().length > 6) {
                            val choice = radioGroup.checkedRadioButtonId
                            val radioButton = findViewById<RadioButton>(choice)
                            saveUserData(
                                FirstNameEdit.text.toString(),
                                MiddleNameEdit.text.toString(),
                                LastNameEdit.text.toString(),
                                BirthdayEdit.text.toString(),
                                LocationNameEdit.text.toString(),
                                emailEdit.text.toString(),
                                PhoneNumberEdit.text.toString(),
                                PasswordEdit.text.toString(),
                                radioButton.text.toString()
                            )
                        } else
                            Toast.makeText(this, "Phone must be at least 7", Toast.LENGTH_SHORT)
                                .show()
                    } else
                        Toast.makeText(this, "email not contain @ character !!", Toast.LENGTH_SHORT)
                            .show()
                } else
                    Toast.makeText(this, "password not confirmed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserData(
        first_name: String, middle_name: String, last_name: String, Birthday: String,
        location: String, email: String, Phone_number: String, password: String, kind: String
    ) {

        val const = Constants(this)
        const.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = hashMapOf(
                        "first_name" to first_name,
                        "middle_name" to middle_name,
                        "last_name" to last_name,
                        "Birthday" to Birthday,
                        "location" to location,
                        "email" to email,
                        "Phone_number" to Phone_number,
                        "password" to password,
                        "kind_of_account" to kind,
                        "Courses" to hashMapOf<String,HashMap<String,*>>()
                    )

                    val shared = getSharedPreferences("shared", MODE_PRIVATE).edit()
                    const.db.collection("users")
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
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        BirthdayEdit.text = "${p2 + 1}/$p3/$p1"
    }
}