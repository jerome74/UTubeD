package com.wlp.utubed.util

import android.app.Activity
import android.os.Build
import android.widget.TextView
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
            target.writeBytes(mp4Stream)
            return
        }

        val source = kotlin.io.createTempFile("videos_",".mp4", File(downloadDir))

        source.writeBytes(mp4Stream)

        AudioExtractor().genVideoUsingMuxer(source.absolutePath, target.absolutePath, -1, -1, true, false);

        source.delete()
    }
}