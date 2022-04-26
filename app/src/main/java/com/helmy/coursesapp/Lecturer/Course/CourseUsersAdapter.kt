package com.helmy.coursesapp.Lecturer.Course

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.course_users_template.view.*

class CourseUsersAdapter(var cont: Context, var arr: ArrayList<Uuser>) :
    RecyclerView.Adapter<CourseUsersAdapter.viewHolder>() {

    class viewHolder(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(
            LayoutInflater.from(cont).inflate(R.layout.course_users_template, parent, false)
        )
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.U_Name.text = arr[position].name
        holder.itemView.setOnClickListener {
            val i = Intent(cont, ChatWithStudent::class.java)
            i.putExtra("ReceiverEmail", arr[position].email)
            cont.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return arr.size
    }
}