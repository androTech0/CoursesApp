package com.helmy.coursesapp.Classes

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.left_chat.view.*
import kotlinx.android.synthetic.main.right_chat.view.*

class ChatAdapter(val context: Context, val ll: ArrayList<MsgClass>) :
    RecyclerView.Adapter<ChatAdapter.viewHolder>() {

    private val currentUEmail = Constants(context).auth.currentUser!!.email

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
