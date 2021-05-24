package com.vtsb.hipago.di.module

import com.vtsb.hipago.util.helper.Wrapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class WrapperModule {

    // todo : change it to safe mechanism
    // instead of injecting value from outside,
    // get value from inside directly

    @Provides
    @Singleton
    @Named("languageWrapper")
    fun provideLanguageWrapper(): Wrapper<String> =
        Wrapper("korean")

    @Provides
    @Singleton
    @Named("useLanguageWrapper")
    fun provideUseLanguageWrapper(@Named("languageWrapper") languageWrapper: Wrapper<String>): Wrapper<String> =
        Wrapper(languageWrapper.value)

    @Provides
    @Singleton
    @Named("optionLRThumbnailWrapper")
    fun provideOptionLRThumbnailWrapper(): Wrapper<Boolean> =
        Wrapper(false)

    @Provides
    @Singleton
    @Named("optionImagePreviewWrapper")
    fun provideOptionImagePreviewWrapper(): Wrapper<Boolean> =
        Wrapper(true)

    @Provides
    @Singleton
    @Named("optionGalleryViewTypeWrapper")
    fun provideOptionGalleryViewTypeWrapper(): Wrapper<Int> =
        Wrapper(1)


}