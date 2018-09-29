package com.example.jovel.learnux_kotlin.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.jovel.learnux_kotlin.R
import com.example.jovel.learnux_kotlin.model.Video
import com.example.jovel.learnux_kotlin.utils.OnVideoClickListener

class VideoHolder(view: View) : RecyclerView.ViewHolder(view){

    private var mThumbnailIV: ImageView? = null

    private var mTitleTV: TextView? = null
    private var mDescriptionTV: TextView? = null
    private var mDurationTV: TextView? = null
    private var mFrame: RelativeLayout? = null


    fun bindVideo(video: Video, onVideoClickListener: OnVideoClickListener?){

        mThumbnailIV = itemView.findViewById(R.id.row_thumbnail)
        mTitleTV = itemView.findViewById(R.id.row_title)
        mDescriptionTV = itemView.findViewById(R.id.row_description)
        mDurationTV = itemView.findViewById(R.id.row_duration)

        mTitleTV!!.text = video.title
        mDescriptionTV!!.text = video.description
        mDurationTV!!.text = video.duration

        mFrame = itemView.findViewById(R.id.row_frame)

        Glide.with(mThumbnailIV!!.context)
                .load(video.thumbnail)
                .into(mThumbnailIV!!)

        mFrame!!.setOnClickListener {
            onVideoClickListener!!.onVideoClick(video)
        }
    }
}