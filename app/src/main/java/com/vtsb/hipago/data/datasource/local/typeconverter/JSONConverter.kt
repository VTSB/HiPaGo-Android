package com.vtsb.hipago.data.datasource.local.typeconverter

import androidx.room.TypeConverter
import org.json.JSONObject

class JSONConverter {

    @TypeConverter
    fun fromJSONString(value: String?): JSONObject? {
        return value?.let { JSONObject(value) }
    }

    @TypeConverter
    fun jsonObjectToString(date: JSONObject?): String? {
        return date?.toString()
    }

}