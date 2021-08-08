package com.wlp.utubed.model

import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject

class BaseStringPostRequest constructor(
    uri: String
    , val model: IModel
    , val contentType: String
    , responseOK: Response.Listener<String>
    , responseError: Response.ErrorListener
    , val mapHeaders: MutableMap<String, String>?
) :
    StringRequest(
        Method.POST
        , uri
        , responseOK
        , responseError
    ) {


    override fun getBody(): ByteArray {
        if (model == null)
            return super.getBody();
        else
            return model.toRequest().toByteArray();
    }

    override fun getBodyContentType(): String {
        return contentType
    }

    override fun getHeaders(): MutableMap<String, String> {
        if (mapHeaders == null)
            return super.getHeaders()
        else
            return mapHeaders!!
    }
}