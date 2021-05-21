package com.vtsb.hipago.domain.entity

import java.util.*

data class GalleryBlock(val no: Long, val title: String, val date: Date, val tags: Map<TagType, List<String>>, )
