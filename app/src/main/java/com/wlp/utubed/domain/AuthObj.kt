package com.wlp.utubed.domain

import com.wlp.utubed.util.ThisApplication
import com.wlp.utubed.util.ThreadProgressBar

object AuthObj
{
    var token
        get() = ThisApplication.shardPrefs.token()!!
        set(value) = ThisApplication.shardPrefs.token(value)

    var isLoggIn
        get() = ThisApplication.shardPrefs.isLoggIn()!!
        set(value) = ThisApplication.shardPrefs.isLoggIn(value)

    var email
        get() = ThisApplication.shardPrefs.email()!!
        set(value) = ThisApplication.shardPrefs.email(value)

    var notify
        get() = ThisApplication.shardPrefs.notify()!!
        set(value) = ThisApplication.shardPrefs.notify(value)

    var thread : ThreadProgressBar? = null
    var fileTypeDownloadVideo = ""


    fun reset()
    {
        token  = ""
        isLoggIn = false
        fileTypeDownloadVideo = ""
        thread = null
    }
}