package com.wlp.utubed.adapters

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkResponse
import com.squareup.picasso.Picasso
import com.wlp.utubed.R
import com.wlp.utubed.UTubeDActivity
import com.wlp.utubed.domain.AuthObj
import com.wlp.utubed.model.CompleteObj
import com.wlp.utubed.models.DownloadVideo
import com.wlp.utubed.models.Video
import com.wlp.utubed.services.VideoService
import com.wlp.utubed.util.*
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

            val permissionCheck_WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission((context as UTubeDActivity),
            Manifest.permission.WRITE_EXTERNAL_STORAGE);

            val permissionCheck_READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission((context as UTubeDActivity),
                Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionCheck_WRITE_EXTERNAL_STORAGE==PackageManager.PERMISSION_GRANTED
                && permissionCheck_READ_EXTERNAL_STORAGE==PackageManager.PERMISSION_GRANTED){
                //this means permission is granted and you can do read and write
            }else{
                ActivityCompat.requestPermissions((context as UTubeDActivity),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_WRITE_PERMISSION);
            }

            val builder = AlertDialog.Builder(context)
            val dialogView = layoutInflater.inflate(R.layout.video_dialog_download, null)

            dialogView.findViewById<TextView>(R.id.video_tv_channel).text = video.channelTitle
            dialogView.findViewById<TextView>(R.id.video_tv_length).text = video.length
            dialogView.findViewById<TextView>(R.id.video_tv_title).text = video.title





            Picasso.with(context).load(video.thumbnails).into(dialogView.findViewById<ImageView>(R.id.video_img_thumb))


            builder.setView(dialogView)

                .setPositiveButton(context.getString(R.string.download), { dialog: DialogInterface?, which: Int ->

                    val localIntent = Intent(BROADCAST_DOWNLOAD_VIDEO)

                    localIntent.putExtra(INTENT_ID_VIDEO,video.id)
                    localIntent.putExtra(INTENT_TITLE,video.title)

                   LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
                })
                .setNegativeButton(context.getString(R.string.cancel), { dialog: DialogInterface?, which: Int ->

                }).create().show()
        })

    }
}