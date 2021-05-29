package com.vtsb.hipago.domain.entity

import java.sql.Date

data class GalleryBlock(
    val id: Int,
    val type: GalleryBlockType,
    val title: String,
    val date: Date,
    val tags: Map<TagType, List<String>>,
    val thumbnail: String,
    val related: List<Int>) {

    override fun equals(other: Any?): Boolean =
        other != null && other is GalleryBlock &&
        id == other.id && type == other.type &&
        title == other.title

    override fun hashCode(): Int =
        (type.hashCode()) * 31 + id

}
