package com.example.jovel.learnux_kotlin.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jovel.learnux_kotlin.R
import com.example.jovel.learnux_kotlin.config.Config
import com.example.jovel.learnux_kotlin.utils.OnVideoExitListener
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment

private const val VIDEO_ARG = "id"

class PlaybackFragment : Fragment(), YouTubePlayer.OnInitializedListener {

    private var mPlayer: YouTubePlayer? = null
    private var mOnVideoExitListener: OnVideoExitListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        mOnVideoExitListener = context as OnVideoExitListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_playback, container, false)

        val frag = YouTubePlayerSupportFragment()
        frag.initialize(Config.YOUTUBE_API, this)

        val ft = fragmentManager!!.beginTransaction()
        ft.add(R.id.frag_container, frag).commit()

        return v
    }

    override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, player: YouTubePlayer?, wasRestored: Boolean) {
        mPlayer = player
        mPlayer!!.setFullscreen(false)
        mPlayer!!.setShowFullscreenButton(false)

        if(!wasRestored) {
            playVideo()
        }
    }

    override fun onInitializationFailure(p0: YouTubePlayer.Provider?, youTubeInitializationResult: YouTubeInitializationResult?) {
        if (!youTubeInitializationResult!!.isUserRecoverableError) {
            youTubeInitializationResult.getErrorDialog(activity, 1).show()
        }
    }

    fun pauseVideo(){
        if (mPlayer != null) {
            mPlayer!!.pause()
        }
    }

    fun playVideo(){
        mPlayer!!.loadVideo(arguments!!.getString(VIDEO_ARG))
        mPlayer!!.play()
    }
}