package com.vtsb.hipago.data.initializer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class Initializer {

    @Inject
    lateinit var databaseInitializer: DatabaseInitializer

    fun init() {
        CoroutineScope(Dispatchers.IO).launch {
            databaseInitializer.init()
        }
    }

}