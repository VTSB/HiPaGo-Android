package com.vtsb.hipago.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vtsb.hipago.data.datasource.local.dao.GalleryBlockDao
import com.vtsb.hipago.data.datasource.local.entity.*
import com.vtsb.hipago.data.datasource.local.entity.relation.GalleryDataTagDataCrossRef
import com.vtsb.hipago.data.datasource.local.converter.DateConverter
import com.vtsb.hipago.data.datasource.local.converter.GalleryBlockTypeConverter
import com.vtsb.hipago.data.datasource.local.converter.JSONConverter


@Database(entities = [
    GalleryData::class,
    //GalleryTag::class,
    LanguageTag::class,
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
public abstract class AppDatabase : RoomDatabase() {

    public abstract fun galleryBlockDao(): GalleryBlockDao

}