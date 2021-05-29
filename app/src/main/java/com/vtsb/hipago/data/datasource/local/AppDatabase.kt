package com.vtsb.hipago.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vtsb.hipago.data.datasource.local.converter.DateConverter
import com.vtsb.hipago.data.datasource.local.converter.GalleryBlockTypeConverter
import com.vtsb.hipago.data.datasource.local.converter.JSONConverter
import com.vtsb.hipago.data.datasource.local.dao.GalleryBlockDao
import com.vtsb.hipago.data.datasource.local.dao.TagDao
import com.vtsb.hipago.data.datasource.local.entity.*
import com.vtsb.hipago.data.datasource.local.entity.relation.GalleryDataTagDataCrossRef


@Database(entities = [
    GalleryData::class,
    //GalleryTag::class,
    GalleryRelated::class,
    TagData::class,
    TagDataLocal::class,
    TagDataTransform::class,

    GalleryDataTagDataCrossRef::class,
    ], version = 1, exportSchema = false)
@TypeConverters(value = [
    DateConverter::class,
    GalleryBlockTypeConverter::class,
    JSONConverter::class,
])
abstract class AppDatabase : RoomDatabase() {

    abstract fun galleryBlockDao(): GalleryBlockDao

    abstract fun tagDao(): TagDao

}