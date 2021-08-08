package com.wlp.utubed.controller.handler

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.wlp.utubed.UTubeDActivity
import com.wlp.utubed.util.BROADCAST_LOGIN

class SplashHandler(val context: Context, val activity: Activity) : Handler()
{


    override fun handleMessage(msg: Message) {

        var localIntent : Intent? = null;

        super.handleMessage(msg)

        when(msg.what){
            0->  localIntent = Intent(context, UTubeDActivity::class.java)
            1 -> LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(BROADCAST_LOGIN))
        }

        context.startActivity(localIntent!!)
        activity.finish()

    }
}