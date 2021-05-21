package com.vtsb.hipago.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.Index


@Entity(
    tableName = "language_tag",
    inheritSuperIndices = true,
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class LanguageTag(
    val id: Long,
    val name: String,
    val local: String,
)
