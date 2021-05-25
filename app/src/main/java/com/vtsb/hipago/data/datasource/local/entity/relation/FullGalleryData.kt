package com.vtsb.hipago.data.datasource.local.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.vtsb.hipago.data.datasource.local.entity.GalleryRelated

data class FullGalleryData(
    @Embedded val galleryDataWithTagData: GalleryDataWithTagData,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
    )
    val related: List<GalleryRelated>
)
