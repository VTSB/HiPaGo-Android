package com.vtsb.hipago.di.module

import com.vtsb.hipago.data.repository.GalleryBlockRepositoryImpl
import com.vtsb.hipago.data.repository.GalleryNumberRepositoryImpl
import com.vtsb.hipago.domain.repository.GalleryBlockRepository
import com.vtsb.hipago.domain.repository.GalleryNumberRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGalleryBlockRepository(galleryBlockRepositoryImpl: GalleryBlockRepositoryImpl): GalleryBlockRepository


    @Binds
    @Singleton
    abstract fun bindGalleryNumberRepository(galleryNumberRepositoryImpl: GalleryNumberRepositoryImpl): GalleryNumberRepository

}