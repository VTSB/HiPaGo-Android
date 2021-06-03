package com.vtsb.hipago

import android.app.Application
import com.vtsb.hipago.di.provider.MyContentProvider
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val hiltEntryPoint = EntryPointAccessors.fromApplication(this, MyContentProvider.SingletonContentProviderEntryPoint::class.java)

        val initializer = hiltEntryPoint.initializer()
        initializer.init()
    }

}

// korean first letter search.
// todo : local korean search.
// more optimized
// https://linuxism.ustd.ip.or.kr/1451


