package com.helmy.coursesapp.LecturerFragments

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import com.helmy.coursesapp.R
import kotlinx.android.synthetic.main.activity_show_video.*

class ShowVideo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_video)


        val videoId = intent.getStringExtra("VideoUrl").toString()


        videoee.setVideoURI(Uri.parse(videoId))
        val mc = MediaController(this)
        videoee.setMediaController(mc)
        mc.setAnchorView(videoee)
    }
}