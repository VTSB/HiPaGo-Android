package com.vtsb.hipago.data.datasource.remote.service

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface GalleryDataService {

    @GET("galleries/{no}.js")
    fun getGalleryJsonData(@Path("no") no: Long): Single<ResponseBody>

    @GET("galleryblock/{no}.html")
    fun getGalleryBlock(@Path("no") no: Long): Single<ResponseBody>

    @GET("{type}-{language}.nozomi")
    fun getNumbersFromType(
        @Path("type") type: String,
        @Path("language") language: String,
        @Header("range") range: String
    ): Single<Response<ResponseBody>>

    @GET("{type}/{tag}-{language}.nozomi")
    fun getNumbers(
        @Path("type") type: String,
        @Path("tag") tag: String,
        @Path("language") language: String,
        @Header("range") range: String
    ): Single<Response<ResponseBody>>

    @GET("{type}/{tag}-{language}.nozomi")
    fun getNumbers(
        @Path("type") type: String,
        @Path("tag") tag: String,
        @Path("language") language: String
    ): Single<Response<ResponseBody>>

    @GET("{name}/version")
    fun getIndexVersion(@Path("name") name: String, @Query("") time: Long): Call<ResponseBody>

    @GET("language_support.js")
    fun getAllLanguages(): Single<ResponseBody>

    @GET("{paths}")
    fun get_url_at_range(
        @Path("paths") url: String,
        @Header("Range") range: String
    ): Call<ResponseBody>

    @GET("{paths}")
    fun get_galleryids_from_nozomi(@Path("paths") url: String): Call<ResponseBody>

}