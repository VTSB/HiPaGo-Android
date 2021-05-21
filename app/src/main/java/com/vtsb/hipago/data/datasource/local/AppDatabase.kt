package com.vtsb.hipago.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vtsb.hipago.data.datasource.local.entity.*
import org.intellij.lang.annotations.Language


@Database(entities = [
    GalleryData::class,
    GalleryTag::class,
    LanguageTag::class,
    TagData::class,
    TagDataLocal::class,
    TagDataTransform::class, ], version = 1)
@TypeConverters(value = [

])
public abstract class AppDatabase : RoomDatabase() {


}