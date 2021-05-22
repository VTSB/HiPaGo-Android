package com.vtsb.hipago.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tag_data_local",
    inheritSuperIndices = true,
    indices = [
        Index(value = ["tagId", "language"], unique = true),
        Index(value = ["language"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = TagData::class,
            parentColumns = ["tagId"],
            childColumns = ["tagId"]
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
    val tagId: Long,
    val language: Long,
    val local: String
)