package com.vtsb.hipago.data.datasource.remote.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GalleryService {

    @GET
    fun getAllLanguages(): Call<ResponseBody>

    @GET("all{typeName}-{firstAlphabet}.html")
    fun getAllSpecificAlphabetTags(
        @Path("typeName") typeName: String,
        @Path("firstAlphabet") firstAlphabet: Char
    ): Call<ResponseBody>

    @GET("{url}")
    fun getResponseBody(@Path("url") url: String): Call<ResponseBody>


}