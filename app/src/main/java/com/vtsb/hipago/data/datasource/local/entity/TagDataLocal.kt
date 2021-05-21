package com.vtsb.hipago.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tag_data_local",
    inheritSuperIndices = true,
    foreignKeys = [
        ForeignKey(
            entity = TagData::class,
            parentColumns = ["tagId"],
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
    @PrimaryKey val id: Long,
    val language: Long,
    val local: String
)