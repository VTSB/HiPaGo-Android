package com.vtsb.hipago.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "language_tag",
    inheritSuperIndices = true,
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class LanguageTag(
    @PrimaryKey val id: Long,
    val name: String,
    val local: String,
)
