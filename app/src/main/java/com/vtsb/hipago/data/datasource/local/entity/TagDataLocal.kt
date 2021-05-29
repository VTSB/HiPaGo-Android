package com.vtsb.hipago.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tag_data_local",
    inheritSuperIndices = true,
    indices = [Index(value = ["local"])],
    foreignKeys = [
        ForeignKey(
            entity = TagData::class,
            parentColumns = ["tagId"],
            childColumns = ["tagId"]
        ),
    ]
)
data class TagDataLocal(
    @PrimaryKey val tagId: Long,
    val local: String
)