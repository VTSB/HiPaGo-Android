package com.vtsb.hipago.data.datasource.local.converter

import androidx.room.TypeConverter
import com.vtsb.hipago.domain.entity.GalleryBlockType

class GalleryBlockTypeConverter {


    @TypeConverter
    fun toGalleryBlockType(id: Byte?): GalleryBlockType? =
        when(id?.toInt()) {
            -1 -> GalleryBlockType.FAILED
            0 -> GalleryBlockType.LOADING
            1 -> GalleryBlockType.MI_DETAILED
            2 -> GalleryBlockType.MI_NOT_DETAILED
            else -> null
        }

    @TypeConverter
    fun fromGalleryBlockType(type: GalleryBlockType?): Byte? =
        type?.id


}