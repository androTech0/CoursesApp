package com.helmy.coursesapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ktx.toObject
import com.helmy.coursesapp.LecturerFragments.Course.CourseData
import com.helmy.coursesapp.LecturerFragments.CoursesFragment
import com.helmy.coursesapp.LecturerFragments.EditFragment
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.courses_template.view.*

class TestActivity : AppCompatActivity() {

    lateinit var const: Constants
    private var myAdapter: FirestoreRecyclerAdapter<CourseData, CoursesFragment.ViewH>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        const = Constants(this)

        getAllCourses()

    }

    override fun onStart() {
        super.onStart()
        myAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        myAdapter!!.stopListening()
    }

    private fun getAllCourses() {
        val const = Constants(this)

        val query = const.db.collection("Courses")

        val option =
            FirestoreRecyclerOptions.Builder<CourseData>().setQuery(query, CourseData::class.java)
                .build()
        myAdapter = object : FirestoreRecyclerAdapter<CourseData, CoursesFragment.ViewH>(option) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): CoursesFragment.ViewH {
                val i = LayoutInflater.from(this@TestActivity)
                    .inflate(R.layout.courses_template, parent, false)
                return CoursesFragment.ViewH(i)
            }

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(
                holder: CoursesFragment.ViewH,
                position: Int,
                model: CourseData
            ) {

                holder.itemView.name.text = model.CourseName
                holder.itemView.num4videos.text = model.NumberOfVideos.toString()
                if (model.CourseImage.isNotEmpty()) {
                    holder.itemView.image.load(model.CourseImage)
                }


                holder.itemView.num4videos.text = model.NumberOfVideos.toString()
            }
        }
        c_Recycle.apply {
            layoutManager =
                LinearLayoutManager(this@TestActivity, LinearLayoutManager.VERTICAL, false)
            adapter = myAdapter
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.custom_menu, menu)
        val b = menu?.findItem(R.id.nav)
        val searchView = b?.actionView as SearchView
        searchView.queryHint = "Search"

        searchView.setOnCloseListener {
            gatAll()
            return@setOnCloseListener false
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(txt: String?): Boolean {
                if (txt!!.isNotEmpty()) {
                    searchFor(txt)
                } else {
                    gatAll()
                }
                return false
            }

            override fun onQueryTextChange(txt: String?): Boolean {
                if (txt!!.isNotEmpty()) {
                    searchFor(txt)
                } else {
                    gatAll()
                }
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)

    }

    private fun searchFor(text: String) {
        val array = ArrayList<CourseData>()

            const.db.collection("Courses").get().addOnSuccessListener { ar ->
                ar.forEach { co ->
                    val course = co.toObject<CourseData>()

                    if (course.CourseName.contains(text)) {
                        array.add(course)

                    }
                }
            }

        c_Recycle.apply {
            layoutManager = LinearLayoutManager(
                this@TestActivity,
                LinearLayoutManager.VERTICAL, false
            )
            adapter = adapterr(this@TestActivity, array)
        }
    }

    private fun gatAll() {
        val array = ArrayList<CourseData>()
        Thread {
            const.db.collection("Courses").get().addOnSuccessListener { ar ->
                ar.forEach { co ->
                    val course = co.toObject<CourseData>()
                    array.add(course)

                }
            }
        }.start()

        c_Recycle.apply {
            layoutManager = LinearLayoutManager(
                this@TestActivity,
                LinearLayoutManager.VERTICAL, false
            )
            adapter = adapterr(this@TestActivity, array)
        }
    }

    class adapterr(val context: Context, val List: ArrayList<CourseData>) :
        RecyclerView.Adapter<adapterr.viewHolder>() {

        class viewHolder(I: View) : RecyclerView.ViewHolder(I) {
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {

            val II = LayoutInflater.from(context).inflate(R.layout.courses_template, parent, false)
            return viewHolder(II)
        }

        override fun onBindViewHolder(holder: viewHolder, pos: Int) {
            holder.itemView.name.text = List[pos].CourseName
            holder.itemView.image.load(List[pos].CourseImage)
            holder.itemView.num4videos.text = List[pos].NumberOfVideos.toString()


//            holder.itemView.setOnClickListener {
//                val i = Intent(context,Activity_Detalies::class.java)
//                i.putExtra("title",List[pos].title)
//                context.startActivity(i)
//            }


//            holder.itemView.option.setOnClickListener {
//                val btn = holder.itemView.option
//                val pop  = PopupMenu(context,btn)
//                pop.menuInflater.inflate(R.menu.option ,pop.menu)
//                pop.setOnMenuItemClickListener {x ->
//                    when(x.itemId){
//                        R.id.Delete -> deleteNote(pos)
//                        R.id.copy ->CopyText(pos)
//                        //         R.id.update -> updatee(pos,List[pos].title,List[pos].body)
//
//                    }
//                    true
//                }
//                pop.show()
//            }
        }
//        fun CopyText(pos: Int){
//
//            val texttocopy = List[pos].title +"\n"+ List[pos].body
//            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            val clipData = ClipData.newPlainText("text",texttocopy)
//            clipboardManager.setPrimaryClip(clipData)
//
//            Toast.makeText(context, "Copied ", Toast.LENGTH_SHORT).show()
//
//        }


        override fun getItemCount(): Int {
            return List.size

        }


    }


}