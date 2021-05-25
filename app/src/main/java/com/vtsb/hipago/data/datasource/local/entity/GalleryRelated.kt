package com.vtsb.hipago.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "gallery_related",
    inheritSuperIndices = true,
    indices = [Index(value = ["id"])],
    foreignKeys = [
        ForeignKey(
            entity = GalleryData::class,
            parentColumns = ["id"],
            childColumns = ["id"]
        )
    ],
)
data class GalleryRelated(
    @PrimaryKey(autoGenerate = true) val idx: Long?,
    val id: Long,
    val related: Long
)
