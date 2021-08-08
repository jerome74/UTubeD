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
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.wlp.utubed.R
import com.wlp.utubed.UTubeDActivity
import com.wlp.utubed.util.BROADCAST_FOLDER
import com.wlp.utubed.util.BROADCAST_LOGIN
import com.wlp.utubed.util.INTENT_FOLDER
import com.wlp.utubed.util.ToastCustom
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

                setEventClick(folder_img,f.name)
                setEventClick(tilte_folder_tv,f.name)
            }
            else{
                if(f.name.endsWith(".mp3") || f.name.endsWith(".mp4")){

                    val contentUri = FileProvider.getUriForFile(context, "com.wlp.utubed", f);

                    val file_p = BitmapFactory.decodeResource(context.resources, context.resources.getIdentifier("file_p", "mipmap", context.packageName))
                    folder_img.setImageBitmap(file_p);

                    folder_img.setOnClickListener {

                        if(f.name.endsWith("mp3")) {

                            setEventClickPlay(folder_img, contentUri , "audio/*", f.name)
                            setEventClickPlay(tilte_folder_tv, contentUri , "audio/*", f.name)
                        }
                        else if(f.name.endsWith("mp4")) {

                            setEventClickPlay(folder_img, contentUri , "video/*", f.name)
                            setEventClickPlay(tilte_folder_tv, contentUri , "audio/*", f.name)
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

    /**
     *
     */

    fun setEventClick(view : View, folder: String) {

        val localIntent = Intent(BROADCAST_FOLDER)

        localIntent.putExtra(INTENT_FOLDER, folder )

        view?.setOnClickListener({

            if(!folder.equals(context.getString(R.string.double_dot))
                &&!folder.equals(context.getString(R.string.no_access)))
                    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
        })

    }

    /**
     *
     */

    fun setEventClickPlay(view : View, uriFile : Uri , dataAndType : String, fileName : String){
        view?.setOnClickListener({

                try{

                    val viewMediaIntent = Intent(Intent.ACTION_VIEW)
                    viewMediaIntent.setDataAndType(uriFile, dataAndType)
                    viewMediaIntent.addFlags( Intent.FLAG_GRANT_WRITE_URI_PERMISSION or  Intent.FLAG_GRANT_READ_URI_PERMISSION )
                    context.startActivity(viewMediaIntent);
                }catch (e : Exception){

                    ToastCustom.show(context as UTubeDActivity,context.getString(R.string.play_error,fileName))
                }

        })
    }
}