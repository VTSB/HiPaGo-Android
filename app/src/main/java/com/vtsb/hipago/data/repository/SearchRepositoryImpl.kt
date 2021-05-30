package com.vtsb.hipago.data.repository

import com.vtsb.hipago.data.mapper.GalleryDataServiceMapper
import com.vtsb.hipago.data.mapper.TagDaoMapper
import com.vtsb.hipago.domain.entity.Suggestion
import com.vtsb.hipago.domain.repository.SearchRepository
import com.vtsb.hipago.util.converter.QueryConverter
import com.vtsb.hipago.util.converter.TagConverter
import java.io.IOException
import java.security.NoSuchAlgorithmException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val tagDaoMapper: TagDaoMapper,
    private val galleryDataServiceMapper: GalleryDataServiceMapper,
    private val queryConverter: QueryConverter,
    private val tagConverter: TagConverter,
) : SearchRepository {


    override fun getSuggestionList(query: String): List<Suggestion>? {
        val lastQuery: String = queryConverter.getLastQuery(query)



        return try {
            galleryDataServiceMapper.getSuggestionForQuery(lastQuery)
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }

    }

}