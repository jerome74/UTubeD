package com.wlp.utubed.adapters

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wlp.utubed.R
import com.wlp.utubed.models.Video
import java.net.URL

class ListVideosAdapter(val context : Context, val videos : List<Video>) :  RecyclerView.Adapter<ListVideosAdapter.Holder>() {

    inner class Holder (itemView: View?) : RecyclerView.ViewHolder(itemView!!)
    {

        val layoutInflater = context.getSystemService( Context.LAYOUT_INFLATER_SERVICE ) as LayoutInflater

        fun bindProduct(video: Video, context: Context)
        {
            val videos_tv_title     = itemView?.findViewById<TextView>(R.id.videos_tv_title)
            val videos_tv_author       = itemView?.findViewById<TextView>(R.id.videos_tv_author)
            val videos_tv_length       = itemView?.findViewById<TextView>(R.id.videos_tv_length)
            val videos_img_thumb    = itemView?.findViewById<ImageView>(R.id.videos_img_thumb)


            Picasso.with(context).load(video.thumbnails).into(videos_img_thumb)

            videos_tv_title?.text   = video.title
            videos_tv_author?.text  = video.channelTitle
            videos_tv_length.text   = video.length

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.videos_item_list ,parent,false)


        val reservationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {}}

        return Holder(view)
    }

    override fun getItemCount(): Int {
        return videos.count()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindProduct(videos[position], context)
        val item = videos[position]
    }
}