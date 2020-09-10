package com.wlp.utubed.services

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.wlp.utubed.domain.AuthObj
import com.wlp.utubed.model.BaseStringPostRequest
import com.wlp.utubed.models.FindVideos
import com.wlp.utubed.util.URI_FIND_VIDEOS

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
            , Response.ErrorListener { error -> complete(false, error.message!!) } , mapHeader)

        baseStringRequest.setRetryPolicy(DefaultRetryPolicy(
                30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))

        Volley.newRequestQueue(context).add(baseStringRequest)

    }
}