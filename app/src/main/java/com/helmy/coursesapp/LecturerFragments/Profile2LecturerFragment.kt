package com.helmy.coursesapp.LecturerFragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.helmy.coursesapp.Constants
import com.helmy.coursesapp.R
import com.helmy.coursesapp.UserData
import kotlinx.android.synthetic.main.fragment_profile2_lecturer.*
import java.util.*

class Profile2LecturerFragment : Fragment(), DatePickerDialog.OnDateSetListener {




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile2_lecturer, container, false)
    }

    override fun onResume() {
        super.onResume()
        BirthdayNameLabel.setOnClickListener {
            val datePikerDialog = DatePickerDialog(
                requireContext(), this,
                2010,
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            datePikerDialog.show()
        }
        getUserInfo()

        UpdateButton.setOnClickListener {
            updateUserInfo(
                FirstNameEdit.text.toString(),
                MiddleNameEdit.text.toString(),
                LastNameEdit.text.toString(),
                BirthdayEdit.text.toString(),
                LocationNameEdit.text.toString(),
                PhoneNumberEdit.text.toString(),
                PasswordEdit.text.toString()
            )
        }

    }

    private fun updateUserInfo(
        first_name: String, middle_name: String,
        last_name: String, Birthday: String, location: String,
        Phone_number: String, password: String
    ) {
         val const = Constants(requireActivity())
        val email = const.auth.currentUser!!.email
        const.db.collection("users").document(email.toString())
            .update(
                "first_name", first_name,
                "middle_name", middle_name,
                "last_name", last_name,
                "Birthday", Birthday,
                "location", location,
                "email", email,
                "Phone_number", Phone_number,
                "password", password
            )
    }

    private fun getUserInfo() {

         val const = Constants(requireActivity())


        val email = const.auth.currentUser!!.email.toString()
        const.db.collection("users").document(email).get().addOnSuccessListener { document ->
            if (document != null) {
                val userData = document.toObject<UserData>()
                FirstNameEdit.setText(userData!!.first_name)
                MiddleNameEdit.setText(userData.middle_name)
                LastNameEdit.setText(userData.last_name)
                BirthdayEdit.text = userData.Birthday
                LocationNameEdit.setText(userData.location)
                emailEdit.setText(userData.email)
                PhoneNumberEdit.setText(userData.Phone_number)
                PasswordEdit.setText(userData.password)
                confirmPasswordEdit.setText(userData.password)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        BirthdayEdit.text = "${p2 + 1}/$p3/$p1"
    }


}