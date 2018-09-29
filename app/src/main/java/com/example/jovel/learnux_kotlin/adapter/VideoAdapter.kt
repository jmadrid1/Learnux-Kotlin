package com.example.jovel.learnux_kotlin.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jovel.learnux_kotlin.R
import com.example.jovel.learnux_kotlin.model.Video
import com.example.jovel.learnux_kotlin.utils.OnVideoClickListener

class VideoAdapter(videos: List<Video>, private val context: Context) : RecyclerView.Adapter<VideoHolder>() {

    private var mContext: Context? = null
    private var mVideoList: List<Video>? = null
    private var onVideoClickListener: OnVideoClickListener? = null

    init {
        mContext = context
        mVideoList = videos
        onVideoClickListener = context as OnVideoClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.list_row_video, parent, false)

        return VideoHolder(view)
    }

    override fun getItemCount(): Int {
        return mVideoList!!.size
    }

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        val video: Video = mVideoList!![position]

        holder.bindVideo(video, onVideoClickListener)
    }
}