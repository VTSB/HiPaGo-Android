package com.vtsb.hipago.data.datasource.local.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.vtsb.hipago.data.datasource.local.entity.TagData
import com.vtsb.hipago.data.datasource.local.entity.TagDataLocal

data class LocalTagData(
    @Embedded val tagDataLocal: TagDataLocal,
    @Relation(
        parentColumn = "tagId",
        entityColumn = "tagId"
    )
    val tagData: TagData
)
