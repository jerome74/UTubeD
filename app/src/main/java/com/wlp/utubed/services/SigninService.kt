package com.wlp.utubed.services

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.wlp.utubed.R
import com.wlp.utubed.model.BaseStringPostRequest
import com.wlp.utubed.models.UserSingIn
import com.wlp.utubed.util.URI_SIGNIN

object SigninService {

    fun signinUser(context: Context, userSingIn : UserSingIn , complete : (Boolean, String) -> Unit ){

        var uri : String = URI_SIGNIN

        val baseStringPostLoginRequest : BaseStringPostRequest = BaseStringPostRequest(
            uri
            ,userSingIn
            , "application/json; charset=utf-8"
            , Response.Listener<String> {
                    response -> complete(true, response)}
            , Response.ErrorListener { error ->
                try {
                    complete(false, error.message!!)
                }catch (e : Exception){
                    complete(false, context.getString(R.string.msg_signin_fail))
                }
            } , null )

        baseStringPostLoginRequest.setRetryPolicy(
            DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        )
        Volley.newRequestQueue(context).add(baseStringPostLoginRequest)

    }

}