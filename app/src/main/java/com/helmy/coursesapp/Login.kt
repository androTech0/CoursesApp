package com.helmy.coursesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.PasswordEdit
import kotlinx.android.synthetic.main.activity_login.emailEdit
import kotlinx.android.synthetic.main.activity_sign_up.*

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()

        signupGo.setOnClickListener {
            startActivity(Intent(this,SignUp::class.java))
        }

        LoginButton.setOnClickListener {
            if(emailEdit.text.isNotEmpty() && PasswordEdit.text.isNotEmpty()){
                if(emailEdit.text.toString().contains("@")){
                    loginToUserAccount(emailEdit.text.toString(),PasswordEdit.text.toString())
                }else{
                    Toast.makeText(this, "email not contain @ character !!", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(baseContext, "You didn't fill all fields !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun loginToUserAccount(email:String,password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Authentication Successful.", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    val email = user!!.email
                    val docRef = db.collection("users").document(email.toString())
                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
//                                Toast.makeText(baseContext, "${document.data}", Toast.LENGTH_SHORT).show()
                                val userData = document.toObject<UserData>()
                                Toast.makeText(baseContext, userData!!.first_name, Toast.LENGTH_SHORT).show()
                                if(userData.kind_of_account == "student"){
                                    startActivity(Intent(this,StudentMainActivity::class.java))
                                }else if(userData.kind_of_account == "lecturer"){
                                    startActivity(Intent(this,LecturerMainActivity::class.java))
                                }
                            } else {
                                Toast.makeText(baseContext, "Document empty.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { _ ->
                            Toast.makeText(baseContext, "Document failed.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}