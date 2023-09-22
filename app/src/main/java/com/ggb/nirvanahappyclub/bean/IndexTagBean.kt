package com.ggb.nirvanahappyclub.bean



import com.ggb.common_library.utils.json.JsonArray
import java.io.Serializable

class IndexTagBean (
//    @SerializedName("tagId")val tagId:String,
    val id:String,
//    @SerializedName("tagName")val tagName:String,
    val tagName:String,
    val description:String,
    val hot:Int,
    val blogs: JsonArray,

    var isSelected:Boolean = true

)

