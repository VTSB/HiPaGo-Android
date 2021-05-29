package com.vtsb.hipago.data.datasource.local.converter

import androidx.room.TypeConverter
import org.json.JSONObject

class JSONConverter {

    @TypeConverter
    fun toJSONObject(value: String?): JSONObject? {
        return value?.let { JSONObject(value) }
    }

    @TypeConverter
    fun fromJSONObject(jsonObject: JSONObject?): String? {
        return jsonObject?.toString()
    }

}