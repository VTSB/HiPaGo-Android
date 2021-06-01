package com.vtsb.hipago.data.mapper

import com.google.common.collect.BiMap
import com.vtsb.hipago.data.datasource.local.entity.pojo.TagDataWithLocal
import com.vtsb.hipago.data.datasource.remote.entity.GalleryBlockWithOtherData
import com.vtsb.hipago.data.datasource.remote.entity.GalleryInfo
import com.vtsb.hipago.data.datasource.remote.service.GalleryDataService
import com.vtsb.hipago.data.datasource.remote.service.converter.*
import com.vtsb.hipago.data.datasource.remote.service.original.ResultJs
import com.vtsb.hipago.data.datasource.remote.service.original.SearchJs
import com.vtsb.hipago.domain.entity.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
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

    fun getGalleryImages(id: Int): GalleryImages {
        val responseBody = galleryDataService.getGalleryJsonData(id).execute().body()
        val jsonString = responseBody!!.string()

        val idx1 = jsonString.indexOf('{')
        val idx2 = jsonString.lastIndexOf('}')

        val jsonObject = JSONObject(jsonString.substring(idx1, idx2 + 1))

        val info = jsonObjectConverter.toGalleryInfo(jsonObject)
        // change
        val list: MutableList<GalleryImage> = java.util.ArrayList()
        for(file in info.files) {
            val imageTypes: MutableSet<ImageType> = hashSetOf(ImageType.ORIGINAL)
            if (file.haswebp == 1) imageTypes.add(ImageType.WEBP)
            if (file.hasavif == 1) imageTypes.add(ImageType.AVIF)

            list.add(GalleryImage(file.name, file.hash, imageTypes))
        }

        return GalleryImages(id, list)
    }

    fun getNotDetailed(id: Int): GalleryBlockWithOtherData =
        elementsConverter.toGalleryBlockNotDetailed(
            responseBodyConverter.toElements(
                galleryDataService.getGalleryBlock(id).execute().body()!!), id)

    fun getAllLanguageTags(): List<TagDataWithLocal> =
        stringConverter.toLanguageTagList(galleryDataService.getAllLanguages().execute().body()!!.string())

    fun getLanguageTagAmount(language: String): Int =
        responseConverter.toContentLength(
            galleryDataService.getNumbersFromType("index", language, "bytes=0-0").execute().raw()) / 4

    fun getNumbers(type: String, language: String, doLoadLength: Boolean = false): GalleryIds =
        getNumbers(galleryDataService.getNumbersFromType(type, language, null), doLoadLength)

    fun getNumbers(type: String, language: String, from: Int, to: Int, doLoadLength: Boolean = false): GalleryIds =
        getNumbers(galleryDataService.getNumbersFromType(type, language, "bytes=$from-$to"), doLoadLength)

    fun getNumbers(type: String, tag: String, language: String, doLoadLength: Boolean = false): GalleryIds =
        getNumbers(galleryDataService.getNumbers(type, tag, language), doLoadLength)

    fun getNumbers(type: String, tag: String, language: String, from: Int, to: Int, doLoadLength: Boolean = false): GalleryIds =
        getNumbers(galleryDataService.getNumbers(type, tag, language, "bytes=$from-$to"), doLoadLength)

    private fun getNumbers(call: Call<ResponseBody>, doLoadLength: Boolean): GalleryIds {
        val response = call.execute()
        val r = response.raw()
        val responseBody = response.body()
        var numberTotalLength = 0
        if (doLoadLength) numberTotalLength = responseConverter.toContentLength(r) / 4

        if (responseBody == null) return GalleryIds(ArrayList(), numberTotalLength)
        val numbers: List<Int> = responseBodyConverter.toIntegerArrayList(responseBody)

        return GalleryIds(numbers, numberTotalLength)
    }


}
