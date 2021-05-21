package com.vtsb.hipago.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "tag_data_local",
    inheritSuperIndices = true,
    foreignKeys = [
        ForeignKey(
            entity = TagData::class,
            parentColumns = ["id"],
            childColumns = ["id"]
        ),
        ForeignKey(
            entity = LanguageTag::class,
            parentColumns = ["id"],
            childColumns = ["language"]
        )
    ]
)
data class TagDataLocal(
    val id: Long,
    val language: Long,
    val local: String
)