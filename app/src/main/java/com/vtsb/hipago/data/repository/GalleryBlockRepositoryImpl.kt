package com.vtsb.hipago.data.repository

import android.util.Log
import com.google.common.collect.BiMap
import com.vtsb.hipago.util.converter.TagConverter
import com.vtsb.hipago.data.mapper.GalleryBlockDaoMapper
import com.vtsb.hipago.data.mapper.GalleryDataServiceMapper
import com.vtsb.hipago.data.mapper.GalleryServiceMapper
import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.domain.entity.GalleryBlockType
import com.vtsb.hipago.domain.repository.GalleryBlockRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.sql.Date
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class GalleryBlockRepositoryImpl @Inject constructor(
    private val galleryBlockDaoMapper: GalleryBlockDaoMapper,
    private val galleryServiceMapper: GalleryServiceMapper,
    private val galleryDataServiceMapper: GalleryDataServiceMapper,
    @Named("tagLocalizationBiMap") private val tagLocalizationBiMap: Array<BiMap<String, String>>,
    private val tagConverter: TagConverter,
) : GalleryBlockRepository {

    override fun getGalleryBlock(id: Int, save: Boolean, skipDB: Boolean): SharedFlow<GalleryBlock> {

        //val flow = MutableStateFlow(GalleryBlock(id, GalleryBlockType.LOADING, "", Date(0), mapOf(), "", LinkedList()))
        val flow = MutableSharedFlow<GalleryBlock>(1, 1, BufferOverflow.DROP_OLDEST)


        CoroutineScope(Dispatchers.IO).launch {
            if (!skipDB) {
                getFromDB(id, save, flow)
            } else {
                getNotDetailed(id, save, flow)
            }
        }

        return flow
    }

    private suspend fun getFromDB(id: Int, save: Boolean, flow: MutableSharedFlow<GalleryBlock>) {
        val galleryBlockWithOtherData = galleryBlockDaoMapper.getGalleryBlock(id)
        if (galleryBlockWithOtherData == null) {
            getNotDetailed(id, save, flow)
        }
        else {
            val galleryBlock = galleryBlockWithOtherData.galleryBlock
            flow.emit(galleryBlock)
            if (galleryBlock.type == GalleryBlockType.MI_NOT_DETAILED) {
                getDetailed(id, save, galleryBlock, galleryBlockWithOtherData.detailedURL, flow)
            }
        }
    }

    private suspend fun getNotDetailed(id: Int, save: Boolean, flow: MutableSharedFlow<GalleryBlock>) {
        try {
            val galleryBlockWithOtherData = galleryDataServiceMapper.getNotDetailed(id)
            flow.emit(galleryBlockWithOtherData.galleryBlock)
            getDetailed(id, save, galleryBlockWithOtherData.galleryBlock, galleryBlockWithOtherData.detailedURL, flow)
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "$id", e)
            flow.emit(GalleryBlock(id, GalleryBlockType.FAILED, "", Date(0), mapOf(), "", LinkedList()))
        }
    }

    private suspend fun getDetailed(id: Int, save: Boolean, prevGalleryBlock: GalleryBlock, url:String, flow: MutableSharedFlow<GalleryBlock>) {
        try {
            val galleryBlock = galleryServiceMapper.getDetailedGalleryBlock(id, url)
            flow.emit(galleryBlock)
            if (save) save(galleryBlock, url)
        } catch (e: Exception) {
            if (save) save(prevGalleryBlock, url)
        }
    }

    private fun save(galleryBlock: GalleryBlock, detailedUrl: String) {
        try {
            galleryBlockDaoMapper.save(galleryBlock, detailedUrl)
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "failed to save galleryBlock(${galleryBlock.id}) : ${e.message}")
        }
    }


}