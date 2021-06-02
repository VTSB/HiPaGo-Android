package com.vtsb.hipago.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "initialize_log",
    inheritSuperIndices = true
)
data class InitializeLog(
    @PrimaryKey val tag: String,
    val data: String
)
