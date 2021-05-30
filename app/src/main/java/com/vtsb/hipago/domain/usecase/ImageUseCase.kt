package com.vtsb.hipago.domain.usecase

import com.vtsb.hipago.domain.repository.GalleryImageRepository
import javax.inject.Inject

class ImageUseCase @Inject constructor(
    private val galleryImageRepository: GalleryImageRepository
) {

    fun loadList(id: Int) =
        galleryImageRepository.loadList(id)

}