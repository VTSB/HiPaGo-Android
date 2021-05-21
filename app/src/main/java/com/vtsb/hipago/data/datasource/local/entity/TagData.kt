package com.vtsb.hipago.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.vtsb.hipago.domain.entity.TagType

@Entity(
    tableName = "tag_data",
    inheritSuperIndices = true,
    indices=[
        Index(value = ["type", "name"], unique = true),
        Index(value = ["type"]),
        Index(value = ["name"])
    ]
)
data class TagData(
    @PrimaryKey(autoGenerate = true) val tagId: Long,
    val type: TagType,
    val name: String,
    val amount: Long)