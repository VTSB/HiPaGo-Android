package com.vtsb.hipago.data.datasource.remote.entity

import java.sql.Date

data class GalleryInfo(
    val languageLocalName: String,
    val language: String,
    val date: Date,
    val files: List<GalleryFile>,
    val tags: List<GalleryTagJson>,
    val japaneseTitle: String,
    val title: String,
    val id: Long,
    val type: String,
)
