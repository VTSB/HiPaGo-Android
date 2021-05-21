package com.vtsb.hipago.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "gallery_tag",
    inheritSuperIndices = true,
    indices = [
        Index(value = ["id", "tag"], unique = true),
        Index(value = ["id"]),
        Index(value = ["tag"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = GalleryData::class,
            parentColumns = ["id"],
            childColumns = ["id"]
        ),
        ForeignKey(
            entity = TagData::class,
            parentColumns = ["id"],
            childColumns = ["tag"]
        )
    ]
)
data class GalleryTag(
    @PrimaryKey(autoGenerate = true) val idx: Long,
    val id: Long,
    val tag: Long,
)