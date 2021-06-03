package com.vtsb.hipago.data.repository

import com.vtsb.hipago.data.initializer.InitializationStatus
import com.vtsb.hipago.data.mapper.GalleryDataServiceMapper
import com.vtsb.hipago.data.mapper.TagDaoMapper
import com.vtsb.hipago.domain.entity.Suggestion
import com.vtsb.hipago.domain.repository.SearchRepository
import com.vtsb.hipago.util.converter.QueryConverter
import com.vtsb.hipago.util.converter.TagConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.security.NoSuchAlgorithmException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val tagDaoMapper: TagDaoMapper,
    private val galleryDataServiceMapper: GalleryDataServiceMapper,
    private val queryConverter: QueryConverter,
    private val initializationStatus: InitializationStatus,
) : SearchRepository {


    override fun getSuggestionList(query: String): List<Suggestion> {
        val lastQuery: String = queryConverter.getLastQuery(query)

        if (initializationStatus.getLocalizationCompleted().value) {
            val result = tagDaoMapper.getFromLocal(lastQuery)
            if (result.isNotEmpty()) return result
        }

        if (initializationStatus.getTagCompleted().value) {
            val result = tagDaoMapper.getFromOriginal(lastQuery)
            if (result.isNotEmpty()) return result
        }

        return try {
            ArrayList()
            //galleryDataServiceMapper.getSuggestionForQuery(lastQuery)
        } catch (e: InterruptedException) {
            e.printStackTrace()
            ArrayList()
        } catch (e: IOException) {
            e.printStackTrace()
            ArrayList()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            ArrayList()
        }

    }

}