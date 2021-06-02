package com.vtsb.hipago.data.initializer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InitializationStatus @Inject constructor() {

    private val localizationCompleted = MutableStateFlow(false)
    private val tagCompleted = MutableStateFlow(false)

    suspend fun completeLocalization() {
        localizationCompleted.emit(true)
    }

    suspend fun completeTag() {
        tagCompleted.emit(true)
    }

    fun getLocalizationCompleted(): StateFlow<Boolean> = localizationCompleted
    fun getTagCompleted(): StateFlow<Boolean> = tagCompleted

}