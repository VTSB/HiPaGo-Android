package com.vtsb.hipago.domain.entity

enum class NumberLoadMode constructor(
    val otherName: String
) {
    NEW(""),
    SEARCH("[search]"),
    RECENTLY_WATCHED("[recently_watched]"),
    FAVORITE("[favorite]"),
}