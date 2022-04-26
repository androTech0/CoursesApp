package com.helmy.coursesapp.Log

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.toObject
import com.helmy.coursesapp.*
import com.helmy.coursesapp.Lecturer.LecturerMainActivity
import com.helmy.coursesapp.Student.StudentMainActivity
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    lateinit var const: Constants

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val kind = getSharedPreferences("shared", MODE_PRIVATE).getString("kind", "")

        const = Constants(this)

        if (const.auth.currentUser != null && kind == getString(R.string.lecturer)) {
            startActivity(Intent(this, LecturerMainActivity::class.java))
            finish()

        } else if (const.auth.currentUser != null && kind == getString(R.string.student)) {

            startActivity(Intent(this, StudentMainActivity::class.java))
            finish()
        }

        signupGo.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }

        LoginButton.setOnClickListener {
            if (emailEdit.text.isNotEmpty() && PasswordEdit.text.isNotEmpty()) {
                if (emailEdit.text.toString().contains("@")) {
                    loginToUserAccount(emailEdit.text.toString(), PasswordEdit.text.toString())
                } else {
                    Toast.makeText(this, "email not contain @ character !!", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(baseContext, "You didn't fill all fields !!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun loginToUserAccount(email: String, password: String) {

        val shared = getSharedPreferences("shared", MODE_PRIVATE).edit()

        const.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Authentication Successful.", Toast.LENGTH_SHORT)
                        .show()
                    const.db.collection("users").document(email).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val userData = document.toObject<UserData>()!!

                                if (userData.kind_of_account == getString(R.string.student)) {
                                    startActivity(Intent(this, StudentMainActivity::class.java))
                                    shared.putString("kind", getString(R.string.student)).apply()
                                    finish()

                                } else if (userData.kind_of_account == getString(R.string.lecturer)) {
                                    startActivity(Intent(this, LecturerMainActivity::class.java))
                                    shared.putString("kind", getString(R.string.lecturer)).apply()
                                    finish()
                                }
                            } else {
                                Toast.makeText(this, "Document empty.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        .addOnFailureListener { _ ->
                            Toast.makeText(this, "Document failed.", Toast.LENGTH_SHORT)
                                .show()
                        }
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}