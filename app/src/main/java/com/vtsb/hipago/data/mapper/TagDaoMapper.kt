package com.vtsb.hipago.data.mapper

import androidx.sqlite.db.SimpleSQLiteQuery
import com.vtsb.hipago.data.datasource.local.dao.TagDao
import com.vtsb.hipago.data.datasource.local.entity.pojo.SuggestionLocal
import com.vtsb.hipago.data.datasource.local.entity.pojo.SuggestionOriginal
import com.vtsb.hipago.data.datasource.remote.service.converter.KoreanQueryConverter
import com.vtsb.hipago.domain.entity.Suggestion
import com.vtsb.hipago.domain.entity.TagType
import com.vtsb.hipago.util.Constants.SEARCH_LIMIT
import com.vtsb.hipago.util.converter.TagConverter
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagDaoMapper @Inject constructor(
    private val tagDao: TagDao,
    private val koreanQueryConverter: KoreanQueryConverter,
    private val tagConverter: TagConverter,
) {

    private fun getFromOriginal(text: String): List<Suggestion> {
        var query = "SELECT `tag_data`.`name`, `tag_data`.`amount`, `tag_data`.`type` FROM `tag_data` \n"
        if (text.isNotEmpty()) {
            if (text.indexOf(':') != -1) {
                val args = text.split(":").toTypedArray()
                args[0] = removeHead(args[0])
                args[0] = tagConverter.toOriginalNonNull(args[0], TagType.BEFORE)
                val type: TagType = tagConverter.getType(args[0])
                query += "	WHERE `tag_data`.`type`=${type.id} \n"
                if (args.size > 1) {
                    query += "	AND `tag_data`.`name` LIKE '%${args[1]}%' \n" }
            } else {
                query += "\tWHERE `tag_data`.`name` LIKE '%$text%' \n"
            }
        }
        query += "	ORDER BY `tag_data`.`amount` DESC LIMIT $SEARCH_LIMIT;"

        val simpleSQLiteQuery = SimpleSQLiteQuery(query)
        val suggestionOriginalList: List<SuggestionOriginal> = tagDao.searchEnglish(simpleSQLiteQuery)

        val suggestionList: MutableList<Suggestion> = LinkedList()
        for (suggestionOriginal in suggestionOriginalList) {
            suggestionList.add(
                Suggestion(
                    suggestionOriginal.name,
                    suggestionOriginal.type,
                    suggestionOriginal.amount.toInt()))
        }
        return ArrayList(suggestionList)
    }

    private fun getSuggestionArrayListFromLocal(text: String): ArrayList<Suggestion> {
        // `tag_data_local`.`name`, `tag_data`.`amount`, `tag_type`.`type`
        var query = """SELECT `tag_data_local`.`local`, `tag_data`.`amount`, `tag_data`.`type`
	FROM `tag_data_local` 
	INNER JOIN `tag_data` ON `tag_data_local`.`no`=`tag_data`.`no`
"""
        if (text.isNotEmpty()) {
            if (text.contains(':')) {
                val args = text.split(":").toTypedArray()
                args[0] = removeHead(args[0])
                args[0] = tagConverter.toOriginalNonNull(args[0], TagType.BEFORE)
                val type = tagConverter.getType(args[0])
                query += "	WHERE `tag_data`.`type`=${type.id}\n"
                if (args.size > 1) {
                    query += "	AND ${koreanQueryConverter.makeQuery(args[1], "`tag_data_local`.`local`")}\n"
                }
            } else {
                query += "	WHERE ${koreanQueryConverter.makeQuery(text, "`tag_data_local`.`local`")}\n"
            }
        }
        query += "	ORDER BY `tag_data`.`amount` DESC LIMIT $SEARCH_LIMIT;"
        val simpleSQLiteQuery = SimpleSQLiteQuery(query)
        val suggestionOriginalList: List<SuggestionLocal> = tagDao.searchLocal(simpleSQLiteQuery)
        val suggestionList: MutableList<Suggestion> = LinkedList()
        for (suggestionOriginal in suggestionOriginalList) {
            suggestionList.add(
                Suggestion(
                    suggestionOriginal.local,
                    suggestionOriginal.type,
                    suggestionOriginal.amount.toInt()))
        }
        return ArrayList(suggestionList)
    }


    private fun removeHead(text: String): String {
        if (text.isEmpty()) return text
        val c = text[0]
        return if (c == '-' || c == '^') {
            text.substring(1)
        } else text
    }



}