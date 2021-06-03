package com.vtsb.hipago.domain.entity

data class GalleryImage(
    val name: String,
    val hash: String,
    val width: Int,
    val height: Int,
    val types: Set<ImageType>,
)