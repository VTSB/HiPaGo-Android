package com.vtsb.hipago.domain.repository

import com.vtsb.hipago.domain.entity.GalleryIds
import com.vtsb.hipago.domain.entity.NumberLoadMode

interface GalleryNumberRepository {

    suspend fun getNumbersByPage(loadMode: NumberLoadMode, query: String, language:String, page: Int, doLoadLength: Boolean): GalleryIds

    fun clearBuffer(query: String, language: String)

    fun getLoadModeFromQuery(query: String): Pair<NumberLoadMode, String>

}