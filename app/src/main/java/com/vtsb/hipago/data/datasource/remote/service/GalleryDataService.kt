package com.vtsb.hipago.data.datasource.remote.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface GalleryDataService {

    @GET("galleries/{id}.js")
    fun getGalleryJsonData(@Path("id") no: Int): Call<ResponseBody>

    @GET("galleryblock/{id}.html")
    fun getGalleryBlock(@Path("id") no: Int): Call<ResponseBody>

    @GET("{type}-{language}.nozomi")
    fun getNumbersFromType(
        @Path("type") type: String,
        @Path("language") language: String,
        @Header("range") range: String? = null
    ): Call<ResponseBody>

    @GET("{type}/{tag}-{language}.nozomi")
    fun getNumbers(
        @Path("type") type: String,
        @Path("tag") tag: String,
        @Path("language") language: String,
        @Header("range") range: String? = null
    ): Call<ResponseBody>

    @GET("{name}/version")
    fun getIndexVersion(@Path("name") name: String, @Query("") time: Long): Call<ResponseBody>

    @GET("language_support.js")
    fun getAllLanguages(): Call<ResponseBody>

    @GET("{paths}")
    fun get_url_at_range(
        @Path("paths") url: String,
        @Header("Range") range: String
    ): Call<ResponseBody>

    @GET("{paths}")
    fun get_galleryids_from_nozomi(@Path("paths") url: String): Call<ResponseBody>

}