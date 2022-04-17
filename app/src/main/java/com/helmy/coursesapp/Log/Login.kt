package com.helmy.coursesapp.Log

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.helmy.coursesapp.LecturerMainActivity
import com.helmy.coursesapp.R
import com.helmy.coursesapp.StudentMainActivity
import com.helmy.coursesapp.UserData
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        val kind = getSharedPreferences("shared", MODE_PRIVATE).getString("kind", "")

        if (auth.currentUser != null && kind == getString(R.string.lecturer)) {
            startActivity(Intent(this, LecturerMainActivity::class.java))
            finish()

        } else if (auth.currentUser != null && kind == getString(R.string.student)) {

            startActivity(Intent(this, StudentMainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        signupGo.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
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

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Authentication Successful.", Toast.LENGTH_SHORT)
                        .show()
//                    val user = auth.currentUser
//                    val email = user!!.email
                    val docRef = db.collection("users").document(email)
                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
//                                Toast.makeText(baseContext, "${document.data}", Toast.LENGTH_SHORT).show()
                                val userData = document.toObject<UserData>()
                                Toast.makeText(
                                    baseContext,
                                    userData!!.first_name,
                                    Toast.LENGTH_SHORT
                                ).show()
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
                                Toast.makeText(baseContext, "Document empty.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        .addOnFailureListener { _ ->
                            Toast.makeText(baseContext, "Document failed.", Toast.LENGTH_SHORT)
                                .show()
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}