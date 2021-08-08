package com.wlp.utubed.models

import com.wlp.utubed.model.IModel
import org.json.JSONObject

data class UserSingIn constructor(var nickname: String = ""
                      , var email: String = ""
                      , var password: String = "") : IModel
{
    override fun toRequest() : String {
        return toString()
    }

    override fun toString(): String {

        val jsonObject : JSONObject = JSONObject();

        jsonObject.put("nickname", nickname)
        jsonObject.put("email", email)
        jsonObject.put("password", password)

        return jsonObject.toString()

    }
}