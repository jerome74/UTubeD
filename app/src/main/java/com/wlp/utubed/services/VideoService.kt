package com.wlp.utubed.services

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.wlp.utubed.R
import com.wlp.utubed.UTubeDActivity
import com.wlp.utubed.activities.LoginActivity
import com.wlp.utubed.domain.AuthObj
import com.wlp.utubed.model.BaseStringPostQueryRequest
import com.wlp.utubed.model.BaseStringPostRequest
import com.wlp.utubed.models.DownloadVideo
import com.wlp.utubed.models.FindVideos
import com.wlp.utubed.network.VolleyMultipartRequest
import com.wlp.utubed.util.*

object VideoService
{
    fun findVideos(context: Context, find: FindVideos, complete : (Boolean, String) -> Unit ){

        var uri : String = "$URI_FIND_VIDEOS"

        var mapHeader : MutableMap<String,String> = mutableMapOf();
        mapHeader.put("Authentication","${AuthObj.token}")

        val baseStringRequest : BaseStringPostRequest = BaseStringPostRequest(
            uri
            ,find
            , "application/json; charset=utf-8"
            , Response.Listener<String> {
                    response -> complete(true, response)}
            , Response.ErrorListener {
                    error -> try{complete(false, error.message!!)}catch (e: Exception){
                (context as UTubeDActivity).manageSpinner(true, View.INVISIBLE)
                ToastCustom.show(context,context.getString(R.string.session_exparied))
                LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(BROADCAST_LOGOUT))}
            } , mapHeader)

        baseStringRequest.setRetryPolicy(DefaultRetryPolicy(
                30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))

        Volley.newRequestQueue(context).add(baseStringRequest)

    }

    fun downloadVideo(context: Context, downloadVideo: DownloadVideo , complete : (Boolean, ByteArray) -> Unit ){

        var uri : String = "$URI_DOWNLOAD_VIDEO/mp4"

        var mapHeader : MutableMap<String,String> = mutableMapOf();
        mapHeader.put("Authentication","${AuthObj.token}")

        val volleyMultipartRequest : VolleyMultipartRequest = VolleyMultipartRequest(
            uri
            ,downloadVideo
            , "application/json; charset=utf-8"
            , Response.Listener<NetworkResponse> { response -> complete(true, response.data)}
            , Response.ErrorListener { error ->
                try {
                    complete(false, error.cause.toString().toByteArray())
                }catch (e : java.lang.Exception)
                {
                    ToastCustom.show(context as Activity,context.getString(R.string.error_download))
                }
            } , mapHeader)

        volleyMultipartRequest.setRetryPolicy(DefaultRetryPolicy(
            ((5 * 60) * 1000),
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))

        Volley.newRequestQueue(context).add(volleyMultipartRequest)

    }
}