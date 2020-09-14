package com.wlp.utubed.adapters

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkResponse
import com.squareup.picasso.Picasso
import com.wlp.utubed.R
import com.wlp.utubed.domain.AuthObj
import com.wlp.utubed.model.CompleteObj
import com.wlp.utubed.models.DownloadVideo
import com.wlp.utubed.models.Video
import com.wlp.utubed.services.VideoService
import com.wlp.utubed.util.BROADCAST_DOWNLOAD_VIDEO
import com.wlp.utubed.util.BROADCAST_LOGIN
import com.wlp.utubed.util.PAYLOAD_DOWNLOAD
import com.wlp.utubed.util.ThreadProgressBar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONObject
import java.io.File
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

            setEventClick(videos_img_thumb,video,layoutInflater)

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

    fun setEventClick(img : ImageView, video: Video, layoutInflater : LayoutInflater) {

        img?.setOnClickListener({

            val builder = AlertDialog.Builder(context)
            val dialogView = layoutInflater.inflate(R.layout.video_dialog_download, null)

            dialogView.findViewById<TextView>(R.id.video_tv_channel).text = video.channelTitle
            dialogView.findViewById<TextView>(R.id.video_tv_length).text = video.length
            dialogView.findViewById<TextView>(R.id.video_tv_title).text = video.title





            Picasso.with(context).load(video.thumbnails).into(dialogView.findViewById<ImageView>(R.id.video_img_thumb))


            builder.setView(dialogView)

                .setPositiveButton("Scarica", { dialog: DialogInterface?, which: Int ->
                    (context as Activity).runOnUiThread {

                        (context as Activity).findViewById<ProgressBar>(R.id.pb_download_video).visibility = View.VISIBLE
                        (context as Activity).findViewById<RecyclerView>(R.id.videoListView).isEnabled = false
                        (context as Activity).findViewById<TextView>(R.id.nameFindTxt).isEnabled = false
                        (context as Activity).findViewById<ImageView>(R.id.icon_search_btn).isEnabled = false
                        (context as Activity).findViewById<ImageView>(R.id.mic_search_btn).isEnabled = false

                        val downloadVideo = DownloadVideo(video.id)

                        AuthObj.thread = ThreadProgressBar((context as Activity).findViewById<ProgressBar>(R.id.pb_download_video))
                        AuthObj.thread!!.loading()

                        VideoService.downloadVideo(context
                            , downloadVideo,
                            { esito: Boolean, messaggio: ByteArray ->
                                if (esito) {
                                    if (messaggio.size > 0 ) {

                                        try {

                                            val localIntent = Intent(BROADCAST_DOWNLOAD_VIDEO)

                                            localIntent.putExtra(PAYLOAD_DOWNLOAD, messaggio)
                                            localIntent.putExtra("title", video.title)

                                            LocalBroadcastManager.getInstance(context)
                                                .sendBroadcast(localIntent)

                                        } catch (e: Exception) {
                                            Toast.makeText(
                                                context,
                                                "error : ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            (context as Activity).findViewById<ProgressBar>(R.id.pb_download_video).visibility =
                                                View.INVISIBLE
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "error : $messaggio",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    (context as Activity).findViewById<ProgressBar>(R.id.pbFindVideos).visibility =
                                        View.INVISIBLE
                                }

                            })

                    }})
                .setNegativeButton("cancella", { dialog: DialogInterface?, which: Int ->

                }).create().show()
        })

    }
}