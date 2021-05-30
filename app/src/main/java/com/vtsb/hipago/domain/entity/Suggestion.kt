package com.vtsb.hipago.domain.entity

data class Suggestion(
    val tag: String,
    val tagType: TagType,
    val amount: Int,
)