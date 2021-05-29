package com.vtsb.hipago.data.datasource.remote.service

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface GalleryService {

    @GET
    fun getAllLanguages(): ResponseBody

    @GET("all{typeName}-{firstAlphabet}.html")
    fun getAllSpecificAlphabetTags(
        @Path("typeName") typeName: String,
        @Path("firstAlphabet") firstAlphabet: Char
    ): ResponseBody

    @GET("{url}")
    fun getResponseBody(@Path("url") url: String): ResponseBody


}