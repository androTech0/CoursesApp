package com.helmy.coursesapp.LecturerFragments.Videos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import com.helmy.coursesapp.R

class ShowVideo : AppCompatActivity() {

    private var mPlayer: SimpleExoPlayer? = null
    private lateinit var playerView: PlayerView

    private lateinit var videoURL:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_video)

        playerView = findViewById(R.id.playerView)

        videoURL = intent.getStringExtra("VideoUrl").toString()

    }

    private fun initPlayer() {

        // Create a player instance.
        mPlayer = SimpleExoPlayer.Builder(this).build()

        // Bind the player to the view.
        playerView.player = mPlayer

        //setting exoplayer when it is ready.
        mPlayer!!.playWhenReady = true

        // Set the media source to be played.
        mPlayer!!.setMediaSource(buildMediaSource())

        // Prepare the player.
        mPlayer!!.prepare()

    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24 || mPlayer == null) {
            initPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }


    private fun releasePlayer() {
        if (mPlayer == null) {
            return
        }
        //release player when done
        mPlayer!!.release()
        mPlayer = null
    }

    //creating mediaSource
    private fun buildMediaSource(): MediaSource {
        // Create a data source factory.
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

        // Create a progressive media source pointing to a stream uri.



        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoURL))

        return mediaSource
    }
}