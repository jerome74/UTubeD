package com.wlp.utubed.adapters

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.wlp.utubed.R
import com.wlp.utubed.util.BROADCAST_FOLDER
import com.wlp.utubed.util.BROADCAST_LOGIN
import com.wlp.utubed.util.INTENT_FOLDER
import java.io.File

class FolderListAdapter(val context : Context, val folders : List<String>) :  RecyclerView.Adapter<FolderListAdapter.Holder>() {

    inner class Holder (itemView: View?) : RecyclerView.ViewHolder(itemView!!)
    {

        val layoutInflater = context.getSystemService( Context.LAYOUT_INFLATER_SERVICE ) as LayoutInflater

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bindProduct(folder: String, context: Context)
        {
            val tilte_folder_tv     = itemView?.findViewById<TextView>(R.id.tilte_folder_tv)
            val folder_img    = itemView?.findViewById<ImageView>(R.id.folder_img)

            val f = File(folder)

            tilte_folder_tv.text = f.name

            if(f.isDirectory){

                val folder = BitmapFactory.decodeResource(context.resources, context.resources.getIdentifier("folder", "mipmap", context.packageName))
                folder_img.setImageBitmap(folder)
                setEventClick(folder_img,f.name,layoutInflater)
            }
            else{
                if(f.name.endsWith(".mp3") || f.name.endsWith(".mp4")){
                    val file_p = BitmapFactory.decodeResource(context.resources, context.resources.getIdentifier("file_p", "mipmap", context.packageName))
                    folder_img.setImageBitmap(file_p);

                    folder_img.setOnClickListener {

                        if(f.name.endsWith("mp3")) {

                            val mp3 = Uri.parse("file://${f.absolutePath}")

                            val intent = Intent(Intent.ACTION_VIEW, mp3);

                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setType("audio/mpeg");

                            val chooser = Intent.createChooser(intent, "Play the song with");
                            //context.startActivity(intent);
                            context.startActivity(chooser);
                        }
                    }
                }
                else{
                    val file = BitmapFactory.decodeResource(context.resources, context.resources.getIdentifier("file", "mipmap", context.packageName))
                    folder_img.setImageBitmap(file);
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.folder_item_list ,parent,false)


        val reservationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {}}

        return Holder(view)
    }

    override fun getItemCount(): Int {
        return folders.count()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindProduct(folders[position], context)
        val item = folders[position]
    }

    fun setEventClick(img : ImageView, folder: String, layoutInflater : LayoutInflater) {

        val localIntent = Intent(BROADCAST_FOLDER)

        localIntent.putExtra(INTENT_FOLDER, folder )

        img?.setOnClickListener({

            if(!folder.equals(context.getString(R.string.double_dot))
                &&!folder.equals(context.getString(R.string.no_access)))
                    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
        })

    }
}