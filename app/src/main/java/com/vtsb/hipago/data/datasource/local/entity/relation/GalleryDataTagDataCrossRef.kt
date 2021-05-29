package com.vtsb.hipago.data.datasource.local.entity.relation

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "gallery_data_tag_data_cross_ref",
    inheritSuperIndices = true,
    primaryKeys = ["id", "tagId"],
    indices = [
        Index(value = ["id"]),
        Index(value = ["tagId"]),
    ]
)
data class GalleryDataTagDataCrossRef(
    val id: Long,
    val tagId: Long
)
