package com.vtsb.hipago.domain.entity

data class GalleryImages(
    val id: Long,
    val images: Map<ImageType, GalleryImage>
)
