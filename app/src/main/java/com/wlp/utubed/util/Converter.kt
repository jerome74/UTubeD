package com.wlp.utubed.util

import android.app.Activity
import android.os.Build
import android.os.Environment
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.wlp.utubed.R
import com.wlp.utubed.domain.AuthObj
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

object Converter
{
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @Throws(Exception::class)
    fun mp4ConvertTo(mp4Stream : ByteArray, title : String , activity: Activity , path : String)
    {
        val target = File("$path/${title}.${AuthObj.fileTypeDownloadVideo}")

        if(AuthObj.fileTypeDownloadVideo.equals(FILE_TYPE_VIDEO))
        {
            target.writeBytes(mp4Stream)
            return
        }

        val source = kotlin.io.createTempFile("videos_",".mp4", File(path))

        source.writeBytes(mp4Stream)

        ToastCustom.show(activity,activity.getString(R.string.status_3))

        AudioExtractor().genVideoUsingMuxer(source.absolutePath, target.absolutePath, -1, -1, true, false);

        ToastCustom.show(activity,activity.getString(R.string.status_4))

        source.delete()
    }
}