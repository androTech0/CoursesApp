package com.helmy.coursesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.helmy.coursesapp.Classes.ChatAdapter
import com.helmy.coursesapp.Classes.MsgClass
import kotlinx.android.synthetic.main.activity_chat_with_student.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.left_chat.view.*
import kotlinx.android.synthetic.main.right_chat.view.*

class ChattingActivity : AppCompatActivity() {

    lateinit var currentUserEmail:String
    lateinit var receiverEmail:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_with_student)

        currentUserEmail = Constants(this).auth.currentUser!!.email!!
        receiverEmail = intent.getStringExtra("ReceiverEmail").toString()
        sendBtn.setOnClickListener {
            if (enterMsg.text.isNotEmpty())
                sendMessage(enterMsg.text.toString())
        }
        getAllMessages()

    }

    private fun sendMessage(msg: String) {

        val message = mapOf(
            "message" to msg,
            "sender" to currentUserEmail,
            "receiver" to receiverEmail
        )

        Firebase.database.reference.child("Chats").push().setValue(message)
            .addOnSuccessListener {
                enterMsg.text.clear()
            }.addOnFailureListener {
                Toast.makeText(this, "Error --> $it", Toast.LENGTH_LONG).show()
            }
    }

    private fun getAllMessages() {

        val const = Constants(this)

        val arra = ArrayList<MsgClass>()

        val db = const.rtdb.child("Chats")
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                arra.clear()
                snapshot.children.forEach {

                    val obj = it.getValue(MsgClass::class.java)!!

                    if (obj.sender == currentUserEmail && obj.receiver == receiverEmail ||
                        obj.sender == receiverEmail && obj.receiver == currentUserEmail
                    ) {
                        arra.add(obj)
                    }
                }
                chatRecycle.apply {
                    adapter = ChatAdapter(this@ChattingActivity, arra)
                    layoutManager = LinearLayoutManager(this@ChattingActivity)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }




}