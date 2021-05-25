package com.vtsb.hipago.data.datasource.remote.service.converter

import com.vtsb.hipago.data.datasource.local.entity.pojo.TagDataWithLocal
import com.vtsb.hipago.data.datasource.remote.service.converter.helper.DateHelper
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.sql.Date
import java.text.ParseException
import java.util.*
import javax.inject.Inject

class StringConverter @Inject constructor(
    private val dateHelper: DateHelper
) {

    @Throws(ParseException::class)
    fun toDate(dateString: String): Date {
        val date: java.util.Date = dateHelper.newDateFormat().parse(dateString) ?: throw NullPointerException()
        return Date(date.time)
    }

    fun toLanguageTagList(languageFile: String): List<TagDataWithLocal> {
        val languageTagList: MutableList<TagDataWithLocal> = ArrayList<TagDataWithLocal>()
//        val languageBitNumberHashMap = HashMap<String, Int>()

        var start: Int = languageFile.indexOf('{')
        var end = languageFile.indexOf('}', start)

//        val bitnumber_language = languageFile.substring(start + 1, end)
//        for (bitnumber_language_str in bitnumber_language.split(",").toTypedArray()) {
//            val split = bitnumber_language_str.split(":").toTypedArray()
//            val bitNumber = split[0].substring(1, split[0].length - 1).toInt()
//            val language = split[1].substring(1, split[1].length - 1)
//
//            languageBitNumberHashMap[language] = bitNumber
//        }

        //languageBitNumberHashMap["all"] = -1
        start = languageFile.indexOf('{', end)
        end = languageFile.indexOf('}', start)
        val language_localname = languageFile.substring(start + 1, end)
        for (language_localname_str in language_localname.split(",").toTypedArray()) {
            val split = language_localname_str.split(":").toTypedArray()
            val language = split[0].substring(1, split[0].length - 1)
            val localName = split[1].substring(1, split[1].length - 1)
            //val bitNumber = languageBitNumberHashMap[language]

            val languageTag = TagDataWithLocal(
                //id = bitNumber?.toLong(),
                name = language,
                local = localName
            )
            languageTagList.add(languageTag)
        }

        //Log.d("check", languageTagList.toString());
        return languageTagList
    }

    fun toURL(url: String): String {
        try {
            return URLEncoder.encode(url, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return url
    }

}