package com.vtsb.hipago.data.repository

import android.util.Log
import com.google.common.collect.BiMap
import com.vtsb.hipago.data.mapper.GalleryBlockDaoMapper
import com.vtsb.hipago.data.mapper.GalleryDataServiceMapper
import com.vtsb.hipago.data.mapper.GalleryServiceMapper
import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.domain.entity.GalleryBlockType
import com.vtsb.hipago.domain.repository.GalleryBlockRepository
import com.vtsb.hipago.util.converter.TagConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    override fun getGalleryBlock(id: Int, save: Boolean, skipDB: Boolean): Flow<GalleryBlock> =
        flow {
            val callback: suspend (GalleryBlock) -> Unit = { this.emit(it) }

            if (!skipDB) {
                getFromDB(id, save, callback)
            } else {
                getNotDetailed(id, save, callback)
            }
        }


    private suspend fun getFromDB(id: Int, save: Boolean, callback: suspend (GalleryBlock) -> Unit) {
        val galleryBlockWithOtherData = galleryBlockDaoMapper.getGalleryBlock(id)
        if (galleryBlockWithOtherData == null) {
            getNotDetailed(id, save, callback)
        }
        else {
            val galleryBlock = galleryBlockWithOtherData.galleryBlock
            callback.invoke(galleryBlock)

            if (galleryBlock.type == GalleryBlockType.MI_NOT_DETAILED) {
                getDetailed(id, save, galleryBlock, galleryBlockWithOtherData.detailedURL, callback)
            }
        }
    }

    private suspend fun getNotDetailed(id: Int, save: Boolean, callback: suspend (GalleryBlock) -> Unit) {
        try {
            val galleryBlockWithOtherData = galleryDataServiceMapper.getNotDetailed(id)
            callback.invoke(galleryBlockWithOtherData.galleryBlock)
            getDetailed(id, save, galleryBlockWithOtherData.galleryBlock, galleryBlockWithOtherData.detailedURL, callback)
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "$id", e)
            callback.invoke(GalleryBlock(id, GalleryBlockType.FAILED, "", Date(0), mapOf(), "", LinkedList()))
        }
    }

    private suspend fun getDetailed(id: Int, save: Boolean, prevGalleryBlock: GalleryBlock, url:String, callback: suspend (GalleryBlock) -> Unit) {
        try {
            val galleryBlock = galleryServiceMapper.getDetailedGalleryBlock(id, url)
            callback.invoke(galleryBlock)
            if (save) save(galleryBlock, url)
        } catch (e: Exception) {
            if (save) save(prevGalleryBlock, url)
        }
    }

    private fun save(galleryBlock: GalleryBlock, detailedUrl: String) {
        try {
            galleryBlockDaoMapper.save(galleryBlock, detailedUrl)
        } catch (t: Throwable) {
            Log.e(this.javaClass.simpleName, "failed to save galleryBlock(${galleryBlock.id})", t)
        }
    }


}