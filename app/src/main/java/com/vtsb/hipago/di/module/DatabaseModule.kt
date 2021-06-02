package com.vtsb.hipago.di.module

import android.app.Application
import androidx.room.Room
import com.vtsb.hipago.data.datasource.local.AppDatabase
import com.vtsb.hipago.data.datasource.local.dao.GalleryBlockDao
import com.vtsb.hipago.data.datasource.local.dao.InitializeDao
import com.vtsb.hipago.data.datasource.local.dao.TagDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase =
        Room.databaseBuilder(application, AppDatabase::class.java, "Database")
            .build()

    @Provides
    @Singleton
    fun provideGalleryBlockDao(appDatabase: AppDatabase): GalleryBlockDao =
        appDatabase.galleryBlockDao()

    @Provides
    @Singleton
    fun provideTagDao(appDatabase: AppDatabase): TagDao =
        appDatabase.tagDao()

    @Provides
    fun provideInitializeDao(appDatabase: AppDatabase): InitializeDao =
        appDatabase.initializeDao()



}