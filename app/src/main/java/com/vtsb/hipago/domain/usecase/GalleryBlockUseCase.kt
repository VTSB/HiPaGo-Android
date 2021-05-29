package com.vtsb.hipago.domain.usecase

import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.domain.repository.GalleryBlockRepository
import com.vtsb.hipago.domain.repository.GalleryNumberRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

class GalleryBlockUseCase @Inject constructor(
    private val galleryBlockRepository: GalleryBlockRepository,
    private val galleryNumberRepository: GalleryNumberRepository,
) {



    fun getGalleryBlock(id: Int, save: Boolean=true, skipDB: Boolean=false): StateFlow<GalleryBlock> {
        return galleryBlockRepository.getGalleryBlock(id, save, skipDB)
    }

}