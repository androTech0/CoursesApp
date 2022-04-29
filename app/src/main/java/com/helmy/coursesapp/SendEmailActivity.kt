package com.helmy.coursesapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window.FEATURE_NO_TITLE
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.helmy.coursesapp.Classes.CourseData
import com.helmy.coursesapp.Lecturer.Fragments.LecturerCoursesFragment
import kotlinx.android.synthetic.main.courses_template.view.*
import kotlinx.android.synthetic.main.fragment_courses.*
import kotlinx.android.synthetic.main.send_mail_dialog.*
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class SendEmailActivity : AppCompatActivity() {

    private var myAdapter: FirestoreRecyclerAdapter<CourseData, LecturerCoursesFragment.ViewH>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_email)

        getAllCourses()

        myAdapter!!.startListening()

    }

    override fun onStop() {
        super.onStop()
        myAdapter!!.stopListening()
    }

    private fun getAllCourses() {
        val const = Constants(this)

        val query = const.db.collection("Courses")
            .whereEqualTo("LecturerEmail", const.auth.currentUser!!.email)
        val option =
            FirestoreRecyclerOptions.Builder<CourseData>().setQuery(query, CourseData::class.java)
                .build()
        myAdapter = object : FirestoreRecyclerAdapter<CourseData, LecturerCoursesFragment.ViewH>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LecturerCoursesFragment.ViewH {
                val i = LayoutInflater.from(this@SendEmailActivity)
                    .inflate(R.layout.courses_template, parent, false)
                return LecturerCoursesFragment.ViewH(i)
            }

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(holder: LecturerCoursesFragment.ViewH, position: Int, model: CourseData) {

                holder.itemView.name.text = model.CourseName
                holder.itemView.num4videos.text = model.NumberOfVideos.toString()
                holder.itemView.num4students.text = model.NumberOfStudents.toString()
                if (model.CourseImage.isNotEmpty()) {
                    holder.itemView.image.load(model.CourseImage)
                }
                val idCou = model.CourseId
                holder.itemView.setOnClickListener {
                    val dialog = Dialog(this@SendEmailActivity)
                    dialog.requestWindowFeature(FEATURE_NO_TITLE)
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.send_mail_dialog)

                    val b1 = dialog.findViewById<Button>(R.id.button1)
                    val b2 = dialog.findViewById<Button>(R.id.button2)

                    b1.setOnClickListener {
                        const.db.collection("Courses").whereEqualTo("CourseId", idCou).get()
                            .addOnSuccessListener {
                                val obj: CourseData = it.documents[0].toObject(CourseData::class.java)!!
                                obj.StudentsIDs.forEach { id ->
                                    Toast.makeText(this@SendEmailActivity, id, Toast.LENGTH_SHORT).show()
                                    sendEmail(id,dialog.editText2.text.toString(),dialog.editText3.text.toString())
                                }
                                dialog.dismiss()
                            }
                    }
                    b2.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                }

                holder.itemView.num4videos.text = model.NumberOfVideos.toString()
            }
        }
        CoursesRecycle.apply {
            layoutManager = LinearLayoutManager(this@SendEmailActivity, LinearLayoutManager.VERTICAL, false)
            adapter = myAdapter
        }

    }

    fun sendEmail(mailTo:String,mailSubj:String,mailMsg:String){
        Thread {
            val username = "coursesapp0@gmail.com"
            val password = "courses-app1@"
            val message = mailMsg
            val props = Properties()
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.starttls.enable", "true")
            props.put("mail.smtp.host", "smtp.gmail.com")
            props.put("mail.smtp.port", "587")

            val session: Session = Session.getDefaultInstance(props,
                object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(username, password)
                    }
                })

            try {
                val mm = MimeMessage(session)
                mm.setFrom(InternetAddress(username))
                mm.addRecipients(
                    Message.RecipientType.TO,
                    InternetAddress(mailTo).toString()
                )
                mm.subject = mailSubj
                mm.setText(message)
                Transport.send(mm)
//                Toast.makeText(applicationContext, "done", Toast.LENGTH_SHORT).show()
                Log.e("state","done")
            } catch (e: MessagingException) {
//                Toast.makeText(applicationContext, "error", Toast.LENGTH_SHORT).show()
                Log.e("state",e.message.toString())
            }
            val policy: StrictMode.ThreadPolicy =
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }.start()

    }

}