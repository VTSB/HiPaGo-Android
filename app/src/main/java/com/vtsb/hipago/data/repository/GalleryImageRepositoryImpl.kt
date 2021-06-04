package com.vtsb.hipago.data.repository

import com.vtsb.hipago.data.datasource.remote.service.original.CommonJs
import com.vtsb.hipago.data.mapper.GalleryDataServiceMapper
import com.vtsb.hipago.domain.entity.GalleryImages
import com.vtsb.hipago.domain.repository.GalleryImageRepository
import com.vtsb.hipago.util.helper.FileHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GalleryImageRepositoryImpl @Inject constructor(
    private val galleryDataServiceMapper: GalleryDataServiceMapper,
    private val fileHelper: FileHelper,
    private val commonJs: CommonJs,
) : GalleryImageRepository {

    private val flowBuffer: MutableMap<Int, Flow<GalleryImages>> = ConcurrentHashMap()
    private val dataBuffer: MutableMap<Int, GalleryImages> = ConcurrentHashMap()

    override fun loadList(id: Int): Flow<GalleryImages> {

        val bufferedData = dataBuffer[id]
        if (bufferedData != null) {
            return flowOf(bufferedData)
        }

        val bufferedFlow = flowBuffer[id]
        if (bufferedFlow != null) {
            return bufferedFlow
        }

        val newFlow = flow {
            withContext(Dispatchers.IO) {
                try {
                    val result = galleryDataServiceMapper.getGalleryImages(id)
                    dataBuffer[id] = result
                    emit(result)
                } catch (t: Throwable) {
                    error(t)
                }
            }
            flowBuffer.remove(id)
        }
        flowBuffer[id] = newFlow

        return newFlow
    }




}