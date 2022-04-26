package com.helmy.coursesapp.Lecturer.Course

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.helmy.coursesapp.Constants
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.activity_chat_with_student.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.left_chat.view.*
import kotlinx.android.synthetic.main.right_chat.view.*

class ChatWithStudent : AppCompatActivity() {

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
                    adapter = ChatAdapter(this@ChatWithStudent, arra)
                    layoutManager = LinearLayoutManager(this@ChatWithStudent)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }

    data class MsgClass(
        var message: String = "",
        var receiver: String = "",
        var sender: String = ""
    )

    class ChatAdapter(val context: Context, val ll: ArrayList<MsgClass>) :
        RecyclerView.Adapter<ChatAdapter.viewHolder>() {

        val currentUEmail = Constants(context).auth.currentUser!!.email

        val msgLeft = 0
        val megRight = 1
        var isRight = false

        class viewHolder(I: View) : RecyclerView.ViewHolder(I)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
            return if (viewType == msgLeft) {
                viewHolder(
                    LayoutInflater.from(context).inflate(R.layout.left_chat, parent, false)
                )
            } else {
                viewHolder(
                    LayoutInflater.from(context).inflate(R.layout.right_chat, parent, false)
                )
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: viewHolder, position: Int) {

            if (isRight) {
                holder.itemView.rightMsg.text = ll[position].message

            } else {
                holder.itemView.leftMsg.text = ll[position].message
            }

        }

        override fun getItemCount(): Int {
            return ll.size
        }
        override fun getItemViewType(position: Int): Int {

            return if (ll[position].sender == currentUEmail) {
                isRight = true
                megRight
            } else {
                isRight = false
                msgLeft
            }
        }

    }

}