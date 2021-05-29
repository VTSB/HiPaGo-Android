package com.vtsb.hipago.data.datasource.remote.entity

import com.vtsb.hipago.domain.entity.GalleryBlock

data class GalleryBlockWithOtherData(
    val galleryBlock: GalleryBlock,
    val detailedURL: String
) {
}