package com.vtsb.hipago.data.repository

import android.util.Log
import com.google.common.collect.BiMap
import com.vtsb.hipago.data.converter.TagConverter
import com.vtsb.hipago.data.datasource.local.adapter.GalleryBlockDaoAdapter
import com.vtsb.hipago.data.datasource.remote.adapter.GalleryDataServiceAdapter
import com.vtsb.hipago.data.datasource.remote.adapter.GalleryServiceAdapter
import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.domain.entity.GalleryBlockType
import com.vtsb.hipago.domain.repository.GalleryBlockRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.sql.Date
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class GalleryBlockRepositoryImpl @Inject constructor(
    private val galleryBlockDaoAdapter: GalleryBlockDaoAdapter,
    private val galleryServiceAdapter: GalleryServiceAdapter,
    private val galleryDataServiceAdapter: GalleryDataServiceAdapter,
    @Named("tagLocalizationBiMap") private val tagLocalizationBiMap: Array<BiMap<String, String>>,
    private val tagConverter: TagConverter,
    ) : GalleryBlockRepository {

    override fun getGalleryBlock(id: Int, save: Boolean, skipDB: Boolean): StateFlow<GalleryBlock> {
        val flow = MutableStateFlow(GalleryBlock(id, GalleryBlockType.LOADING, "", Date(0), mapOf(), "", LinkedList()))

        CoroutineScope(Dispatchers.IO).launch {
            if (!skipDB) {
                getFromDB(id, save, flow)
            } else {
                getNotDetailed(id, save, flow)
            }
        }

        return flow
    }

    private suspend fun getFromDB(id: Int, save: Boolean, flow: MutableStateFlow<GalleryBlock>) {
        val galleryBlockWithOtherData = galleryBlockDaoAdapter.getGalleryBlock(id)
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

    private suspend fun getNotDetailed(id: Int, save: Boolean, flow: MutableStateFlow<GalleryBlock>) {
        try {
            val galleryBlockWithOtherData = galleryDataServiceAdapter.getNotDetailed(id)
            flow.emit(galleryBlockWithOtherData.galleryBlock)
            getDetailed(id, save, galleryBlockWithOtherData.galleryBlock, galleryBlockWithOtherData.detailedURL, flow)
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "${e.message}")
            flow.emit(GalleryBlock(id, GalleryBlockType.FAILED, "", Date(0), mapOf(), "", LinkedList()))
        }
    }

    private suspend fun getDetailed(id: Int, save: Boolean, prevGalleryBlock: GalleryBlock, url:String, flow: MutableStateFlow<GalleryBlock>) {
        try {
            val galleryBlock = galleryServiceAdapter.getDetailedGalleryBlock(id, url)
            flow.emit(galleryBlock)
            if (save) save(galleryBlock, url)
        } catch (e: Exception) {
            if (save) save(prevGalleryBlock, url)
        }
    }

    private suspend fun save(galleryBlock: GalleryBlock, detailedUrl: String) {
        try {
            galleryBlockDaoAdapter.save(galleryBlock, detailedUrl)
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "failed to save galleryBlock(${galleryBlock.id}) : ${e.message}")
        }
    }


}