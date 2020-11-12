package com.wlp.ibolletta.models

import com.wlp.ibolletta.model.IModel
import org.json.JSONObject

data class Bolletta constructor(   var id : String
                                      , var cc : String
                                      , var importo : String
                                      , var scadenza : String
                                      , var numero : String
                                      , var owner : String
                                      , var td : String) : IModel


{
    override fun toRequest() : String {
        return toString()
    }

    override fun toString(): String {

        val jsonObject : JSONObject = JSONObject();

        jsonObject.put("cc", cc)
        jsonObject.put("importo", importo)
        jsonObject.put("scadenza", scadenza)
        jsonObject.put("numero", numero)
        jsonObject.put("owner", owner)
        jsonObject.put("td", td)

        return jsonObject.toString()

    }
}