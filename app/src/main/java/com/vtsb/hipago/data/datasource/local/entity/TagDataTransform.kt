package com.vtsb.hipago.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tag_data_transform",
    inheritSuperIndices = true,
    indices = [ Index( value = ["transformed"], unique = true ) ]
)
data class TagDataTransform(
    @PrimaryKey val original: String,
    val transformed: String
)