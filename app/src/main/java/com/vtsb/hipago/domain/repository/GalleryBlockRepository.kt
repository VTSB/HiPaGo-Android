package com.vtsb.hipago.domain.repository

import com.vtsb.hipago.domain.entity.GalleryBlock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface GalleryBlockRepository {

    fun getGalleryBlock(id: Int, save: Boolean=true, skipDB: Boolean=false): Flow<GalleryBlock>

}