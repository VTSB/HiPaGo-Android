package com.vtsb.hipago.domain.repository

import com.vtsb.hipago.domain.entity.GalleryImages
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface GalleryImageRepository {

    fun loadList(id: Int): Flow<GalleryImages>

}