package com.example.myapplication.nadutkin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.nadutkin.R

class UploadsAdapter(private val data: MutableList<Video>) :
    RecyclerView.Adapter<UploadsAdapter.UploadsViewHolder>() {

    inner class UploadsViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {
        fun bind(video: Video) {
            with(root) {
                findViewById<TextView>(R.id.video_name).text = video.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadsViewHolder =
        UploadsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.post_holder, parent, false)
        )

    override fun onBindViewHolder(holder: UploadsViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount(): Int = data.size
}

data class Video(val name: String)