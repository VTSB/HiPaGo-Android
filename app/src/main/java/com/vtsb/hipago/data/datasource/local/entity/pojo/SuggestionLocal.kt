package com.vtsb.hipago.data.datasource.local.entity.pojo

import com.vtsb.hipago.domain.entity.TagType

data class SuggestionLocal(
    val local: String,
    val amount: Long,
    val type: TagType,
)
