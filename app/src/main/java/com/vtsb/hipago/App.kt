package com.vtsb.hipago

import android.app.Application
import com.vtsb.hipago.data.initializer.Initializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Initializer().init()

    }

}

// korean first letter search.
// todo : local korean search.
// more optimized
// https://linuxism.ustd.ip.or.kr/1451


