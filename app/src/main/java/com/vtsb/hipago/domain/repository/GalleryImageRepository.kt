package com.vtsb.hipago.domain.repository

import com.vtsb.hipago.domain.entity.GalleryImages

interface GalleryImageRepository {

    fun loadList(id: Int): GalleryImages

}