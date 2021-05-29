package com.vtsb.hipago.domain.usecase

import com.vtsb.hipago.data.datasource.remote.entity.GalleryNumber
import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.domain.entity.NumberLoadMode
import com.vtsb.hipago.domain.repository.GalleryBlockRepository
import com.vtsb.hipago.domain.repository.GalleryNumberRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GalleryBlockUseCase @Inject constructor(
    private val galleryBlockRepository: GalleryBlockRepository,
    private val galleryNumberRepository: GalleryNumberRepository,
) {

    fun getGalleryNumberListByPage(loadMode: NumberLoadMode, query: String, language:String, page: Int, pageSize: Int, doLoadLength: Boolean): GalleryNumber =
        galleryNumberRepository.getNumbersByPage(loadMode, query, language, page, pageSize, doLoadLength)

    fun getGalleryBlock(id: Int, save: Boolean=true, skipDB: Boolean=false): StateFlow<GalleryBlock> =
        galleryBlockRepository.getGalleryBlock(id, save, skipDB)

    fun clearGalleryNumberBuffer(query: String, language: String) =
        galleryNumberRepository.clearBuffer(query, language)

}