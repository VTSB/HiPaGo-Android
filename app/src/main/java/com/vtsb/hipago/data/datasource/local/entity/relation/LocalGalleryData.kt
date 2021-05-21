package com.vtsb.hipago.data.datasource.local.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.vtsb.hipago.data.datasource.local.entity.GalleryData

data class LocalGalleryData (
    @Embedded val galleryData: GalleryData,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
    )
    val tags: List<GalleryDataWithLocalTagData>
)