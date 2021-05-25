package com.vtsb.hipago.domain.entity

import java.sql.Date

data class GalleryBlock(
    val id: Int,
    val type: GalleryBlockType,
    val title: String,
    val date: Date,
    val tags: Map<TagType, List<String>>,
    val thumbnail: String,
    val related: List<Int>)
