package com.vtsb.hipago.data.datasource.local.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.vtsb.hipago.data.datasource.local.entity.GalleryTag
import com.vtsb.hipago.data.datasource.local.entity.TagData
import com.vtsb.hipago.data.datasource.local.entity.TagDataLocal

data class GalleryDataWithLocalTagData (
    @Embedded val galleryTag: GalleryTag,
    @Relation(
        parentColumn = "tag",
        entityColumn = "tagId"
    )
    val localTagData: LocalTagData
)
