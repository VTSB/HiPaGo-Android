package com.vtsb.hipago.domain.repository

import com.vtsb.hipago.domain.entity.GalleryBlock
import kotlinx.coroutines.flow.StateFlow

interface GalleryBlockRepository {

    fun getGalleryBlock(id: Int, save: Boolean=true, skipDB: Boolean=false): StateFlow<GalleryBlock>

}