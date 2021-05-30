package com.vtsb.hipago.domain.usecase

import com.vtsb.hipago.data.datasource.remote.entity.GalleryNumber
import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.domain.entity.NumberLoadMode
import com.vtsb.hipago.domain.repository.GalleryBlockRepository
import com.vtsb.hipago.domain.repository.GalleryNumberRepository
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class GalleryBlockUseCase @Inject constructor(
    private val galleryBlockRepository: GalleryBlockRepository,
    private val galleryNumberRepository: GalleryNumberRepository,
) {

    fun getGalleryNumberListByPage(loadMode: NumberLoadMode, query: String, language:String, page: Int, doLoadLength: Boolean): GalleryNumber =
        galleryNumberRepository.getNumbersByPage(loadMode, query, language, page, doLoadLength)

    fun getGalleryBlock(id: Int, save: Boolean=true, skipDB: Boolean=false): SharedFlow<GalleryBlock> =
        galleryBlockRepository.getGalleryBlock(id, save, skipDB)

    fun clearGalleryNumberBuffer(query: String, language: String) =
        galleryNumberRepository.clearBuffer(query, language)

    fun getLoadModeFromQuery(query: String): Pair<NumberLoadMode, String> =
        galleryNumberRepository.getLoadModeFromQuery(query)

}