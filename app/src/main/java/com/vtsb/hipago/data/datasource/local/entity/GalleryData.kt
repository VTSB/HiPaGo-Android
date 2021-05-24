package com.vtsb.hipago.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vtsb.hipago.domain.entity.GalleryBlockType
import org.json.JSONObject
import java.sql.Date


@Entity(
    tableName = "gallery_data",
    inheritSuperIndices = true,
)
data class GalleryData (
    @PrimaryKey val id: Long,
    val type: GalleryBlockType,
    val title: String,
    val date: Date,
    val extraData: JSONObject
    )
