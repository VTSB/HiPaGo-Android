package com.vtsb.hipago.domain.entity

data class GalleryImage(
    val name: String,
    val hash: String,
    val types: Set<ImageType>,
)