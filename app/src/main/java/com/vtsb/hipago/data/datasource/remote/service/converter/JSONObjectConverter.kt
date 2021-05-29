package com.vtsb.hipago.data.datasource.remote.service.converter

import com.vtsb.hipago.data.datasource.remote.entity.GalleryFile
import com.vtsb.hipago.data.datasource.remote.entity.GalleryInfo
import com.vtsb.hipago.data.datasource.remote.entity.GalleryTagJson
import org.json.JSONException
import org.json.JSONObject
import java.sql.Date
import java.text.ParseException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JSONObjectConverter @Inject constructor(
    private val stringConverter: StringConverter
) {


    @Throws(JSONException::class, ParseException::class)
    fun toGalleryInfo(jsonObject: JSONObject): GalleryInfo {
        val language_localname = jsonObject.getString("language_localname")
        val language = jsonObject.getString("language")
        val date = jsonObject.getString("date")
        val files = jsonObject.getJSONArray("files")
        val japanese_title = jsonObject.getString("japanese_title")
        val title = jsonObject.getString("title")
        val id = jsonObject.getString("id")
        val type = jsonObject.getString("type")
        val date_new: Date = stringConverter.toDate(date.substring(0, date.lastIndexOf(':')))
        val files_new: ArrayList<GalleryFile> = ArrayList<GalleryFile>()

        var length = files.length()
        for (i in 0 until length) {
            val `object` = files.getJSONObject(i)
            files_new.add(toGalleryFile(`object`))
        }

        val tags_new: ArrayList<GalleryTagJson> = ArrayList<GalleryTagJson>()
        try {
            val tags = jsonObject.getJSONArray("tags")
            length = tags.length()
            for (i in 0 until length) {
                val `object` = tags.getJSONObject(i)
                tags_new.add(toGalleryTagJson(`object`))
            }
        } catch (ignored: JSONException) { }
        return GalleryInfo(
            language_localname,
            language,
            date_new,
            files_new,
            tags_new,
            japanese_title,
            title,
            id.toLong(),
            type
        )
    }

    @Throws(JSONException::class)
    fun toGalleryFile(jsonObject: JSONObject): GalleryFile {
        val width = jsonObject.getInt("width")
        val height = jsonObject.getInt("height")
        val haswebp: Int = try {
            jsonObject.getInt("haswebp")
        } catch (exception: JSONException) {
            0
        }
        val hasavif: Int = try {
            jsonObject.getInt("hasavif")
        } catch (exception: JSONException) {
            0
        }
        val hasavifsmalltn: Int = try {
            jsonObject.getInt("hasavifsmalltn")
        } catch (exception: JSONException) {
            0 // confirmed : this section can exist or not exist
        }
        val name = jsonObject.getString("name")
        val hash = jsonObject.getString("hash")
        return GalleryFile(width, height, haswebp, hasavifsmalltn, hasavif, name, hash)
    }

    @Throws(JSONException::class)
    fun toGalleryTagJson(jsonObject: JSONObject): GalleryTagJson {
        val tagSexual: Byte = try {
            val male = jsonObject.getString("male")
            val female = jsonObject.getString("female")
            if (male == "1") {
                1
            } else if (female == "1") {
                2
            } else {
                0
            }
        } catch (exception: JSONException) {
            0
        }
        val url = jsonObject.getString("url")
        val tag = jsonObject.getString("tag")
        return GalleryTagJson(tagSexual, url, tag)
    }

}