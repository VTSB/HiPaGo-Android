package com.vtsb.hipago.data.datasource.remote.adapter

import com.vtsb.hipago.data.datasource.local.entity.pojo.TagDataWithLocal
import com.vtsb.hipago.data.datasource.remote.entity.GalleryBlockWithOtherData
import com.vtsb.hipago.data.datasource.remote.entity.GalleryInfo
import com.vtsb.hipago.data.datasource.remote.entity.GalleryNumber
import com.vtsb.hipago.data.datasource.remote.service.GalleryDataService
import com.vtsb.hipago.data.datasource.remote.service.converter.*
import com.vtsb.hipago.data.datasource.remote.service.original.ResultJs
import com.vtsb.hipago.data.datasource.remote.service.original.SearchJs
import com.vtsb.hipago.data.datasource.remote.service.original.pojo.Suggestion
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GalleryDataServiceAdapter @Inject constructor(
    private val galleryDataService: GalleryDataService,
    private val resultJs: ResultJs,
    private val searchJs: SearchJs,
    private val responseConverter: ResponseConverter,
    private val responseBodyConverter: ResponseBodyConverter,
    private val elementsConverter: ElementsConverter,
    private val stringConverter: StringConverter,
    private val jsonObjectConverter: JSONObjectConverter,
) {

    fun doSearch(query: String, language: String): List<Int> =
        resultJs.do_search(query, language)

    fun getSuggestionForQuery(query: String): ArrayList<Suggestion> {
        val sug = searchJs.handle_key_up_in_search_box(query)
        return ArrayList(listOf(*sug.arr))
    }

    fun getGalleryInfo(id: Int): GalleryInfo {
        val responseBody = galleryDataService.getGalleryJsonData(id)
        val jsonString = responseBody.string()

        val idx1 = jsonString.indexOf('{')
        val idx2 = jsonString.lastIndexOf('}')

        val jsonObject = JSONObject(jsonString.substring(idx1, idx2 + 1))

        return jsonObjectConverter.toGalleryInfo(jsonObject)
    }

    fun getNotDetailed(id: Int): GalleryBlockWithOtherData =
        elementsConverter.toGalleryBlockNotDetailed(
            responseBodyConverter.toElements(
                galleryDataService.getGalleryBlock(id)), id)

    fun getAllLanguageTags(): List<TagDataWithLocal> =
        stringConverter.toLanguageTagList(galleryDataService.getAllLanguages().string())

    fun getLanguageTagAmount(language: String): Int =
        responseConverter.toContentLength(
            galleryDataService.getNumbersFromType("index", language, "bytes=0-0")
                .raw()) / 4

    fun getNumbers(type: String, language: String, doLoadLength: Boolean = false): GalleryNumber =
        getNumbers(galleryDataService.getNumbersFromType(type, language, null), doLoadLength)

    fun getNumbers(type: String, language: String, from: Int, to: Int, doLoadLength: Boolean = false): GalleryNumber =
        getNumbers(galleryDataService.getNumbersFromType(type, language, "bytes=$from-$to"), doLoadLength)

    fun getNumbers(type: String, tag: String, language: String, doLoadLength: Boolean = false): GalleryNumber =
        getNumbers(galleryDataService.getNumbers(type, tag, language), doLoadLength)

    fun getNumbers(type: String, tag: String, language: String, from: Int, to: Int, doLoadLength: Boolean = false): GalleryNumber =
        getNumbers(galleryDataService.getNumbers(type, tag, language, "bytes=$from-$to"), doLoadLength)

    private fun getNumbers(response: Response<ResponseBody>, doLoadLength: Boolean): GalleryNumber {
        val r = response.raw()
        val responseBody = response.body()
        var numberTotalLength = 0
        if (doLoadLength) numberTotalLength = responseConverter.toContentLength(r) / 4

        if (responseBody == null) return GalleryNumber(ArrayList(), numberTotalLength)
        val numbers: List<Int> = responseBodyConverter.toIntegerArrayList(responseBody)

        return GalleryNumber(numbers, numberTotalLength)
    }


}
