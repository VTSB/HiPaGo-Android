package com.vtsb.hipago.data.repository

import com.vtsb.hipago.data.datasource.remote.service.original.CommonJs
import com.vtsb.hipago.data.mapper.GalleryDataServiceMapper
import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.domain.entity.GalleryImages
import com.vtsb.hipago.domain.repository.GalleryImageRepository
import com.vtsb.hipago.util.helper.FileHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GalleryImageRepositoryImpl @Inject constructor(
    private val galleryDataServiceMapper: GalleryDataServiceMapper,
    private val fileHelper: FileHelper,
    private val commonJs: CommonJs,
) : GalleryImageRepository {

    private val galleryImageBuffer: MutableMap<Int, SharedFlow<GalleryImages>> = ConcurrentHashMap()

    override fun loadList(id: Int): SharedFlow<GalleryImages> {
        val bufferedFlow = galleryImageBuffer[id]
        if (bufferedFlow != null) {
            return bufferedFlow
        }

        val newFlow = MutableSharedFlow<GalleryImages>(1, 1, BufferOverflow.DROP_OLDEST)
        galleryImageBuffer[id] = newFlow

        CoroutineScope(Dispatchers.IO).launch {
            newFlow.emit(galleryDataServiceMapper.getGalleryImages(id))
        }
        return newFlow
    }




}