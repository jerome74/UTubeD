package com.wlp.utubed.models

import com.wlp.utubed.model.IModel
import org.json.JSONObject

data class Video(var id : String = ""
                 ,var title : String = ""
                 ,var thumbnails : String = ""
                 ,var channelTitle : String = ""
                 ,var length : String = ""
                 ,var etag : String = ""
                 ,var kind : String = ""
                 ,var channelId : String = ""
                 ,var description : String = ""


) : IModel{
override fun toRequest() : String {
    return toString()
}

override fun toString(): String {

    val jsonObject : JSONObject = JSONObject();


    jsonObject.put("id", id)
    jsonObject.put("etag", etag)
    jsonObject.put("kind", kind)
    jsonObject.put("channelId", channelId)
    jsonObject.put("channelTitle", channelTitle)
    jsonObject.put("length", length)
    jsonObject.put("description", description)
    jsonObject.put("title", title)
    jsonObject.put("thumbnails", thumbnails)

    return jsonObject.toString()

}
}
