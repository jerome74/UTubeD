package com.wlp.utubed.util

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import com.wlp.utubed.R
import com.wlp.utubed.domain.AuthObj
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

object Converter
{
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Throws(Exception::class)
    fun mp4ConvertTo(mp4Stream : ByteArray, title : String , activity: Activity)
    {
        val downloadDir = "/storage/emulated/0/Download"

        val target = File("$downloadDir/${title}.${AuthObj.fileTypeDownloadVideo}")

        if(AuthObj.fileTypeDownloadVideo.equals(FILE_TYPE_VIDEO))
        {
            activity.tv_event_download.text = activity.getString(R.string.status_5)
            target.writeBytes(mp4Stream)
            activity.tv_event_download.text = activity.getString(R.string.status_6)

            return
        }

        activity.tv_event_download.text = activity.getString(R.string.status_3)

        val source = kotlin.io.createTempFile("videos_",".mp4", File(downloadDir))

        source.writeBytes(mp4Stream)

        AudioExtractor().genVideoUsingMuxer(source.absolutePath, target.absolutePath, -1, -1, true, false);

        source.deleteOnExit()
    }
}