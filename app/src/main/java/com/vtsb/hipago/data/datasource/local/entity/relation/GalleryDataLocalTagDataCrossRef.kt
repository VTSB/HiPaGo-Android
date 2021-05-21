package com.vtsb.hipago.data.datasource.local.entity.relation

import androidx.room.Entity

@Entity(primaryKeys = ["tag", "tagId"])
data class GalleryDataLocalTagDataCrossRef(
    val tag: Long,
    val tagId: Long
)
