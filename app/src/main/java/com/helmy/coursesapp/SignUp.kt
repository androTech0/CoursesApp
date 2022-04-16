package com.helmy.coursesapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUp : AppCompatActivity() , DatePickerDialog.OnDateSetListener{

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()

        BirthdayNameLabel.setOnClickListener {
            val datePikerDialog = DatePickerDialog(this,this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            datePikerDialog.show()
        }

        SignupButton.setOnClickListener {

            if(FirstNameEdit.text.isNotEmpty() &&
                MiddleNameEdit.text.isNotEmpty() &&
                LastNameEdit.text.isNotEmpty() &&
                emailEdit.text.isNotEmpty() &&
                PhoneNumberEdit.text.isNotEmpty() &&
                PasswordEdit.text.isNotEmpty() &&
                confirmPasswordEdit.text.isNotEmpty() &&
                BirthdayEdit.text.isNotEmpty()
            ){
                if(confirmPasswordEdit.text.toString() == PasswordEdit.text.toString()){
                    if(emailEdit.text.toString().contains("@")){
                        if(PhoneNumberEdit.text.toString().length >= 7){
                            sendData(FirstNameEdit.text.toString(),
                                MiddleNameEdit.text.toString(),
                                LastNameEdit.text.toString(),
                                BirthdayEdit.text.toString(),
                                LocationNameEdit.text.toString(),
                                emailEdit.text.toString(),
                                PhoneNumberEdit.text.toString(),
                                PasswordEdit.text.toString())
                        }
                    }else{
                        Toast.makeText(this, "email not contain @ character !!", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "password not confirmed !!", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    fun sendData(first_name:String,middle_name:String,last_name:String,Birthday:String,
                 location:String,email:String,Phone_number:String,password:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(baseContext, "Authentication Successful.",Toast.LENGTH_SHORT).show()
                    // Create a new user with a first and last name
                    val user = hashMapOf(
                        "first name" to first_name,
                        "middle name" to middle_name,
                        "last name" to last_name,
                        "Birthday" to Birthday,
                        "location" to location,
                        "email" to email,
                        "Phone number" to Phone_number,
                        "password" to password
                    )

                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(baseContext, "Store user data Successful.",Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                        }
                            Toast.makeText(baseContext, "Store user data failed.",Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        BirthdayEdit.text = "$p2/$p3/$p1"
    }
}