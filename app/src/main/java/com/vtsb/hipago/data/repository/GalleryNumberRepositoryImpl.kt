package com.vtsb.hipago.data.repository

import com.vtsb.hipago.data.datasource.remote.entity.GalleryNumber
import com.vtsb.hipago.data.mapper.GalleryDataServiceMapper
import com.vtsb.hipago.domain.entity.NumberLoadMode
import com.vtsb.hipago.domain.entity.TagType
import com.vtsb.hipago.domain.repository.GalleryNumberRepository
import com.vtsb.hipago.util.Constants.PAGE_SIZE
import com.vtsb.hipago.util.converter.QueryConverter
import com.vtsb.hipago.util.converter.TagConverter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GalleryNumberRepositoryImpl @Inject constructor(
    private val galleryDataServiceMapper: GalleryDataServiceMapper,
    private val tagConverter: TagConverter,
    private val queryConverter: QueryConverter,
) : GalleryNumberRepository {

    private val galleryNumberBuffer: MutableMap<String, List<Int>> = HashMap()

    override fun getLoadModeFromQuery(query: String): Pair<NumberLoadMode, String> =
        when(val trimQuery = query.trim()) {
            NumberLoadMode.FAVORITE.otherName-> Pair(NumberLoadMode.FAVORITE, trimQuery)
            NumberLoadMode.RECENTLY_WATCHED.otherName-> Pair(NumberLoadMode.RECENTLY_WATCHED, trimQuery)
            ""-> Pair(NumberLoadMode.NEW, "index")
            "index", "popular"-> Pair(NumberLoadMode.NEW, trimQuery)
            else-> {
                if (trimQuery.contains(queryConverter.getChar()) || !trimQuery.contains(':'))
                    Pair(NumberLoadMode.SEARCH, query)
                else
                    Pair(NumberLoadMode.NEW, queryConverter.replace(tagConverter.toOriginalQuery(trimQuery)))
            }
        }

    override fun getNumbersByPage(loadMode: NumberLoadMode, query: String, language:String, page: Int, doLoadLength: Boolean): GalleryNumber {
        val from = (page * PAGE_SIZE)
        val to = from + PAGE_SIZE

        return when (loadMode) {
            NumberLoadMode.SEARCH-> {
                val key = getBufferKey(query, language)
                var buffer = galleryNumberBuffer[key]
                if (buffer == null) {
                    buffer = galleryDataServiceMapper.doSearch(query, language)
                    galleryNumberBuffer[key] = buffer
                }
                return GalleryNumber(buffer.subList(from, to), buffer.size)
            }
            NumberLoadMode.FAVORITE-> {
                GalleryNumber(listOf(), 0)
            }
            NumberLoadMode.RECENTLY_WATCHED-> {
                GalleryNumber(listOf(), 0)
            }
            NumberLoadMode.NEW->{
                val fromByte = from * 4
                val toByte = to * 4 - 1
                when(query) {
                    "index", "popular"->
                        galleryDataServiceMapper.getNumbers(
                            query, language,
                            fromByte, toByte, doLoadLength
                        )
                    else-> {
                        val typeAndTag = query.split(":")
                        when (val tagType = tagConverter.getType(typeAndTag[0])) {
                            TagType.LANGUAGE ->
                                galleryDataServiceMapper.getNumbers(
                                    "index", typeAndTag[1],
                                    fromByte, toByte, doLoadLength
                                )
                            TagType.MALE, TagType.FEMALE ->
                                galleryDataServiceMapper.getNumbers(
                                    tagType.otherName, "${tagType.otherName}:${typeAndTag[1]}",
                                    language, fromByte, toByte, doLoadLength
                                )
                            else ->
                                galleryDataServiceMapper.getNumbers(
                                    tagType.otherName, typeAndTag[1],
                                    language, fromByte, toByte, doLoadLength
                                )
                        }
                    }
                }
            }

        }
    }

    override fun clearBuffer(query: String, language: String) {
        galleryNumberBuffer.remove(getBufferKey(query, language))
    }

    private fun getBufferKey(query: String, language: String) =
        "$language:$query"

}