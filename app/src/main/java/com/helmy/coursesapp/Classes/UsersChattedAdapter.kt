package com.helmy.coursesapp.Classes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.helmy.coursesapp.ChattingActivity
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.course_users_template.view.*

class UsersChattedAdapter(var cont: Context, var arr: ArrayList<Uuser>) :
    RecyclerView.Adapter<UsersChattedAdapter.viewHolder>() {

    class viewHolder(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(
            LayoutInflater.from(cont).inflate(R.layout.course_users_template, parent, false)
        )
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.U_Name.text = arr[position].name
        if (arr[position].image.isNotEmpty()){
            holder.itemView.U_Image.load(arr[position].image)
        }

        holder.itemView.setOnClickListener {
            val i = Intent(cont, ChattingActivity::class.java)
            i.putExtra("ReceiverEmail", arr[position].email)
            cont.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return arr.size
    }
}