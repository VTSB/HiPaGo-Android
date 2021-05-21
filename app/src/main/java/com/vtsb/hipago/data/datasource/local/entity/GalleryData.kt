package com.vtsb.hipago.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date


@Entity(tableName = "gallery_data",
    inheritSuperIndices = true)
data class GalleryData (
    @PrimaryKey val id: Long,
    val title: String,
    val date: Date,
    val etcData: String
    )
