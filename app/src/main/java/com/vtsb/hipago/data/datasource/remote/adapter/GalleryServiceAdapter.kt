package com.vtsb.hipago.data.datasource.remote.adapter

import com.vtsb.hipago.data.datasource.remote.service.GalleryService
import com.vtsb.hipago.data.datasource.remote.service.converter.ElementsConverter
import com.vtsb.hipago.data.datasource.remote.service.converter.ResponseBodyConverter
import okhttp3.ResponseBody
import org.jsoup.select.Elements
import javax.inject.Inject

class GalleryServiceAdapter @Inject constructor(
    private val galleryService: GalleryService,
    private val responseBodyConverter: ResponseBodyConverter,
    private val elementsConverter: ElementsConverter,
) {

    fun getAllLanguages() =
        elementsConverter.toLanguageTagList(
            responseBodyConverter.toElements(
                galleryService.getAllLanguages()))

    fun getAllSpecificAlphabetTags(typeName: String) =
        responseBodyConverter.toElements(
            galleryService.getAllSpecificAlphabetTags(typeName, 'a'))

    fun getDetailedGalleryBlock(id: Int, url: String) =
        elementsConverter.toGalleryBlockDetailed(
            getDetailedGalleryBlockElementRecursive(
                galleryService.getResponseBody(url)), id
        )

    fun getDetailedGalleryBlockElementRecursive(responseBody: ResponseBody): Elements {
        val elements = responseBodyConverter.toElements(responseBody)
        val windowLocation = elementsConverter.toWindowLocation(elements)
        if (windowLocation != null) {
            return getDetailedGalleryBlockElementRecursive(
                galleryService.getResponseBody(
                    windowLocation.substring(18)))
        }
        return elements
    }



}