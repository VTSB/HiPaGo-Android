package com.vtsb.hipago.domain.usecase

import com.vtsb.hipago.domain.entity.GalleryImages
import com.vtsb.hipago.domain.repository.GalleryImageRepository
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class ImageUseCase @Inject constructor(
    private val galleryImageRepository: GalleryImageRepository
) {

    fun loadList(id: Int): SharedFlow<GalleryImages> =
        galleryImageRepository.loadList(id)

}