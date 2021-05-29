package com.vtsb.hipago.data.datasource.local.entity.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.vtsb.hipago.data.datasource.local.entity.GalleryData
import com.vtsb.hipago.data.datasource.local.entity.TagData

data class GalleryDataWithTagData(
    @Embedded val galleryData: GalleryData,
    @Relation(
        entity = TagData::class,
        parentColumn = "id",
        entityColumn = "tagId",
        associateBy = Junction(GalleryDataTagDataCrossRef::class)
    )
    val tagDataList: List<TagData>
)
