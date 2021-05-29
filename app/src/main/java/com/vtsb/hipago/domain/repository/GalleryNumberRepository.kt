package com.vtsb.hipago.domain.repository

import com.vtsb.hipago.data.datasource.remote.entity.GalleryNumber
import com.vtsb.hipago.domain.entity.NumberLoadMode

interface GalleryNumberRepository {

    fun getNumbersByPage(loadMode: NumberLoadMode, query: String, language:String, page: Int, pageSize: Int, doLoadLength: Boolean): GalleryNumber

    fun clearBuffer(query: String, language: String)

    fun getLoadModeFromQuery(query: String): Pair<NumberLoadMode, String>

}