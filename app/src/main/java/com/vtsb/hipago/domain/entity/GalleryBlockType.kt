package com.vtsb.hipago.domain.entity

enum class GalleryBlockType(
    val id: Byte
) {
    FAILED(-1),
    LOADING(0),
    MI_DETAILED(1),
    MI_NOT_DETAILED(2),

}