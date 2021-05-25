package com.vtsb.hipago.domain.usecase

import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.domain.repository.GalleryBlockRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GalleryBlockUseCase @Inject constructor(
    private val galleryBlockRepository: GalleryBlockRepository
) {

    fun getGalleryBlock(id: Int, save: Boolean=true, skipDB: Boolean=false): StateFlow<GalleryBlock> {
        return galleryBlockRepository.getGalleryBlock(id, save, skipDB)
    }

}