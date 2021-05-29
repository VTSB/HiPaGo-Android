package com.vtsb.hipago.domain.entity

enum class TagType(
    val id: Byte,
    val otherName: String
) {
    BEFORE(0, "before"),
    NO(0, "no"),
    TYPE(1, "type"),
    LANGUAGE(2, "language"),
    GROUP(3, "group"),
    ARTIST(4, "artist"),
    SERIES(5, "series"),
    CHARACTER(6, "character"),
    TAG(7, "tag"),
    MALE(8, "male"),
    FEMALE(9, "female"),
}