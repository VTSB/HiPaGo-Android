package com.vtsb.hipago.data.datasource.remote.entity

data class GalleryFile(
    val width: Int, val height: Int,
    val haswebp: Int, val hasavifsmalltn: Int, val hasavif: Int,
    val name: String, val hash: String)
