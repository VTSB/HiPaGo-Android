package com.vtsb.hipago.di.provider

import com.vtsb.hipago.data.initializer.Initializer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


class MyContentProvider {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SingletonContentProviderEntryPoint {
        fun initializer(): Initializer
    }

}