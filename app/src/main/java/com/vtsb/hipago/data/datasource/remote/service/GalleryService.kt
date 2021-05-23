package com.vtsb.hipago.data.datasource.remote.service

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface GalleryService {

    @GET
    fun getAllLanguages(): Single<ResponseBody?>?

    @GET("all{typeName}-{firstAlphabet}.html")
    fun getAllSpecificAlphabetTags(
        @Path("typeName") typeName: String?,
        @Path("firstAlphabet") firstAlphabet: Char
    ): Single<ResponseBody?>?

    @GET("{url}")
    fun getResponseBody(@Path("url") url: String?): Single<ResponseBody?>?


}