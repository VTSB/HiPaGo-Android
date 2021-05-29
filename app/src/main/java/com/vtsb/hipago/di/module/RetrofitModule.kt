package com.vtsb.hipago.di.module

import com.vtsb.hipago.data.datasource.remote.service.GalleryDataService
import com.vtsb.hipago.data.datasource.remote.service.GalleryService
import com.vtsb.hipago.util.Constants.MI_URL
import com.vtsb.hipago.util.Constants.MI_URL_LTN
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    @Provides
    @Singleton
    fun provideGalleryService(): GalleryService =
        Retrofit.Builder()
            .baseUrl(MI_URL)
            .build()
            .create(GalleryService::class.java)

    @Provides
    @Singleton
    fun provideGalleryDataService(): GalleryDataService =
        Retrofit.Builder()
            .baseUrl(MI_URL_LTN)
            .build()
            .create(GalleryDataService::class.java)


}