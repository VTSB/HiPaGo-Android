package com.vtsb.hipago.data.datasource.remote.service.converter

import android.util.Log
import com.vtsb.hipago.data.datasource.local.entity.pojo.TagDataWithLocal
import com.vtsb.hipago.data.datasource.remote.entity.GalleryBlockWithOtherData
import com.vtsb.hipago.data.datasource.remote.entity.TagWithAmount
import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.domain.entity.GalleryBlockType
import com.vtsb.hipago.domain.entity.TagType
import kotlinx.coroutines.flow.MutableStateFlow
import org.jsoup.select.Elements
import java.text.ParseException
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ElementsConverter @Inject constructor(
    private val stringConverter: StringConverter,
    @Named("optionLRThumbnailMSF") private val optionLRThumbnail: MutableStateFlow<Boolean>
) {


    fun toWindowLocation(elements: Elements): String? {
        val e = elements.select("script")[0]
        val text = e.toString()
        val idx = text.indexOf("window.location.href = \"")
        val idx2 = text.lastIndexOf("\"")
        return if (idx < idx2) {
            text.substring(idx + 24, idx2)
        } else null
    }

    fun toGalleryBlockDetailed(elements: Elements, id: Int): GalleryBlock {

        val related = toRelatedElements(elements.select("script"))
        if (related.isEmpty()) Log.e(ElementsConverter::class.java.name, "failed to get related elements $id")

        val contentElements = elements.select(".content")
        val thumbnail: String = toThumbnail(contentElements.select(".cover img"))
        val artists = getTexts(contentElements.select("h2 .comma-list a"))
        val title: String = getText(contentElements.select("h1 a"))

        val infoElements = contentElements.select(".gallery-info")
        val dateString = infoElements.select(".date").text()
        val date = stringConverter.toDate(dateString.substring(0, dateString.lastIndexOf(':')))

        val trElements = infoElements.select("tr")
        val groups = getTexts(trElements[0].select(".comma-list a"))
        val type: String = getText(trElements[1].select("a"))
        val language = getText(trElements[2].select("a"))
        val series = getTexts(trElements[3].select(".comma-list a"))
        val characters = getTexts(trElements[4].select(".tags a"))
        val tags = getTexts(trElements[5].select(".tags a"))

        return GalleryBlock(
            id, GalleryBlockType.MI_DETAILED, title, date,
            mapOf(
                TagType.TYPE to listOf(type),
                TagType.LANGUAGE to listOf(language),
                TagType.GROUP to groups,
                TagType.ARTIST to artists,
                TagType.SERIES to series,
                TagType.CHARACTER to characters,
                TagType.TAG to tags
            ), thumbnail, related
        )
    }


    @Throws(ParseException::class)
    fun toGalleryBlockNotDetailed(elements: Elements, id: Int): GalleryBlockWithOtherData {

        val thumbnail: String = toThumbnail(elements.select("img"))
        val artists = getTexts(elements.select(".artist-list a"))
        val titleElement = elements.select("h1 a")
        val title: String = getText(titleElement)
        val detailedURL = titleElement.attr("href")

        val contentElements = elements.select(".dj-content")
        val dateString = contentElements.select("p").text()
        val date = stringConverter.toDate(dateString.substring(0, dateString.lastIndexOf(':')))

        // series, type, language, tags
        val trElements = contentElements.select("tr")
        val series = getTexts(trElements[0].select("a"))
        val type = getText(trElements[1].select("a"))
        val language = getText(trElements[2].select("a"))
        val tags = getTexts(trElements[3].select("a"))

        return GalleryBlockWithOtherData(
                GalleryBlock(
                    id, GalleryBlockType.MI_NOT_DETAILED, title, date,
                    mapOf(
                        TagType.TYPE to listOf(type),
                        TagType.LANGUAGE to listOf(language),
                        TagType.ARTIST to artists,
                        TagType.SERIES to series,
                        TagType.TAG to tags
                    ), thumbnail, ArrayList()
                ), detailedURL)
    }

    fun toRelatedElements(elements: Elements): List<Int> {
        val related = ArrayList<Int>()
        for (e in elements) {
            try {
                var text = e.toString()
                val idx = text.indexOf('[')
                val idx2 = text.indexOf(']', idx)
                if (idx != idx2) {
                    text = text.substring(idx + 1, idx2)
                    val s = text.split(",").toTypedArray()
                    if (s.isEmpty()) continue
                    for (value in s) {
                        related.add(value.toInt())
                    }
                    return related
                }
            } catch (ignored: NumberFormatException) { }
        }
        return related
    }

    fun toLanguageTagList(elements: Elements): List<TagDataWithLocal> {
        val tags: ArrayList<TagDataWithLocal> = ArrayList<TagDataWithLocal>()
        val ele = elements.select("#lang #lang-list a")
        val size = ele.size

        var i = 1 // for skip (all) language
        while (i < size) {
            val e = ele[i]
            val original = e.text()
            val href = e.attr("href")
            val english = href.substring(href.indexOf('-') + 1, href.lastIndexOf('-'))
            val languageTag = TagDataWithLocal(name=english, local=original)
            tags.add(languageTag)
            i++
        }
        return tags
    }

    fun toTagWithAmountList(ele: Elements): List<TagWithAmount> {
        val tags: MutableList<TagWithAmount> = ArrayList<TagWithAmount>()
        val elements = ele.select("div .content li")
        val size = elements.size
        for (i in 0 until size) {
            val eleStr = elements[i].text()
            val split = eleStr.lastIndexOf('(')
            val tag = eleStr.substring(0, split - 1)
            val amount = eleStr.substring(split + 1, eleStr.length - 1).toInt()
            tags.add(TagWithAmount(tag, amount))
        }
        return tags
    }

    fun toTagURLList(ele: Elements): List<String> {
        val urlList: MutableList<String> = ArrayList()
        val elements = ele.select("div .page-content a")
        val size = elements.size / 2
        for (i in 0 until size) {
            urlList.add(elements[i].attr("href"))
        }
        return urlList
    }

    fun toThumbnail(element: Elements): String {
        val srcset = element[0].attr("srcset")
        return try {
            val idx = srcset.indexOf(',')
            if (optionLRThumbnail.value) {
                srcset.substring(idx + 1, srcset.lastIndexOf(' '))
            } else {
                srcset.substring(0, srcset.lastIndexOf(' ', idx))
            }
        } catch (exception: IndexOutOfBoundsException) {
            // safe mechanism
            exception.printStackTrace()
            srcset.substring(0, srcset.indexOf(' '))
        } catch (t: Throwable) {
            t.printStackTrace()
            element.attr("src")
        }
    }

    private fun getTexts(element: Elements): LinkedList<String> {
        val length = element.size
        val list = LinkedList<String>()
        for (i in 0 until length) list.add(element[i].text())
        return list
    }

    private fun getText(element: Elements): String =
        element.text().trim { it <= ' ' }


}