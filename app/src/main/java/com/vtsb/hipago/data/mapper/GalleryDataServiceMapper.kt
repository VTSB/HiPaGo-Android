package com.vtsb.hipago.data.mapper

import com.google.common.collect.BiMap
import com.vtsb.hipago.data.datasource.local.entity.pojo.TagDataWithLocal
import com.vtsb.hipago.data.datasource.remote.entity.GalleryBlockWithOtherData
import com.vtsb.hipago.data.datasource.remote.entity.GalleryInfo
import com.vtsb.hipago.data.datasource.remote.entity.GalleryNumber
import com.vtsb.hipago.data.datasource.remote.service.GalleryDataService
import com.vtsb.hipago.data.datasource.remote.service.converter.*
import com.vtsb.hipago.data.datasource.remote.service.original.ResultJs
import com.vtsb.hipago.data.datasource.remote.service.original.SearchJs
import com.vtsb.hipago.data.datasource.remote.service.original.pojo.PojoSuggestion
import com.vtsb.hipago.domain.entity.Suggestion
import com.vtsb.hipago.domain.entity.TagType
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class GalleryDataServiceMapper @Inject constructor(
    private val galleryDataService: GalleryDataService,
    private val resultJs: ResultJs,
    private val searchJs: SearchJs,
    private val responseConverter: ResponseConverter,
    private val responseBodyConverter: ResponseBodyConverter,
    private val elementsConverter: ElementsConverter,
    private val stringConverter: StringConverter,
    private val jsonObjectConverter: JSONObjectConverter,
    @Named("stringTypeBiMap") private val stringType: BiMap<String, TagType>,
) {

    fun doSearch(query: String, language: String): List<Int> =
        resultJs.do_search(query, language)

    fun getSuggestionForQuery(query: String): List<Suggestion> {
        val sug = searchJs.handle_key_up_in_search_box(query)

        val newList: MutableList<Suggestion> = LinkedList()
        for (pojoSug in sug.arr) {
            newList.add(Suggestion(pojoSug.s, stringType[pojoSug.n]!!, pojoSug.t))
        }

        return ArrayList(newList)
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
