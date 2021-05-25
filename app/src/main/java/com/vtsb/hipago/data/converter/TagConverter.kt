package com.vtsb.hipago.data.converter

import com.google.common.collect.BiMap
import com.vtsb.hipago.data.datasource.remote.service.original.helper.QueryHelper
import com.vtsb.hipago.domain.entity.TagType
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class TagConverter @Inject constructor(
    //@Named("tagNumberBiMap") private val tagNumber: Array<BiMap<String, Long>>,
    //@Named("stringTypeBiMap") private val numberType: BiMap<TagType, String>,
    @Named("stringTypeBiMapInv") private val numberTypeInv: BiMap<String, TagType>,
    @Named("tagTransformerBiMap") private val tagTransformer: BiMap<String, String>,
    @Named("tagTransformerBiMapInv") private val tagTransformerInv: BiMap<String, String>,
    @Named("tagLocalizationBiMap") private val tagLocalization: Array<BiMap<String, String>>,
    @Named("tagLocalizationBiMapInv") private val tagLocalizationInv: Array<BiMap<String, String>>,
    private val queryHelper: QueryHelper) {

    // auto : display -> data -> localization -> display
    // FullTag : contains ':'
    // Just : not convert ' ' to '_'

    fun toLocalQueryJust(fullQuery: String): String {
        val queryArray: Array<String> = queryHelper.splitAutoConvert(fullQuery)
        if (queryArray.size == 1 && queryArray[0].isEmpty()) return fullQuery
        for (i in queryArray.indices) {
            queryArray[i] = toLocalFullTag(queryArray[i])
        }
        return queryHelper.justCombine(queryArray)
    }

    fun toOriginalQuery(fullQuery: String): String {
        val queryArray: Array<String> = queryHelper.splitAutoConvert(fullQuery)
        if (queryArray.size == 1 && queryArray[0].isEmpty()) return fullQuery
        for (i in queryArray.indices) {
            queryArray[i] = toOriginalFullTag(queryArray[i])
        }
        return queryHelper.combine(queryArray)
    }

    fun toLocalFullTag(original: String): String {
        val sides = original.split(":").toTypedArray()
        if (sides.size <= 1) {
            return original
        }
        val ns = sides[0]
        val tag = sides[1]
        val localNs = toLocalNonNull(ns, TagType.BEFORE)
        val localTag = toLocalNonNull(tag, ns)
        return "$localNs:$localTag"
    }

    fun toOriginalFullTag(local: String): String {
        val sides = local.split(":").toTypedArray()
        if (sides.size <= 1) {
            return local
        }
        var ns = sides[0]
        var tag = sides[1]
        ns = toOriginalNonNull(ns, TagType.BEFORE)
        tag = toOriginalNonNull(tag, ns)
        return "$ns:$tag"
    }

    fun toLocalAuto(originalList: List<String>, tagType: TagType): List<String> {
        val localList: MutableList<String> = LinkedList()
        for (original in originalList) {
            localList.add(toLocalAuto(original, tagType))
        }
        return localList
    }

    fun toLocalAuto(original: String, tagType: TagType): String {
        val result = toDataTag(tagType, original)
        val newTag = toLocalNonNull(result.tag, tagType)
        val newTagType = result.tagType
        return toDisplayTag(newTagType, newTag).tag
    }

    fun toLocalNonNull(original: String, term: String): String {
        return toLocalNonNull(original, getType(term))
    }

    fun toLocalNonNull(original: String, tagType: TagType): String {
        val local = toLocal(original, tagType)
        return local ?: original
    }

    fun toOriginalNonNull(local: String, term: String): String {
        return toOriginalNonNull(local, getType(term))
    }

    fun toOriginalNonNull(local: String, tagType: TagType): String {
        val original = toOriginal(local, tagType)
        return original ?: local
    }

    fun toLocal(original: String, term: String): String? {
        return toLocal(original, getType(term))
    }

    fun toOriginal(local: String, term: String): String? {
        return toOriginal(local, getType(term))
    }

    fun toLocal(original: String, tagType: TagType): String? {
        return toLocalNoTransform(transform(original), tagType)
    }

    fun toOriginal(local: String, tagType: TagType): String? {
        return toOriginalNoTransform(unTransform(local), tagType)
    }

    fun toLocalNoTransform(original: String?, tagType: TagType): String? {
        return tagLocalization[tagType.id.toInt().coerceAtMost(7)][original]
    }

    fun toOriginalNoTransform(local: String, tagType: TagType): String? {
        return tagLocalizationInv[tagType.id.toInt().coerceAtMost(7)][local]
    }

    fun transform(original: String): String {
        val transformed = tagTransformer[original]
        return transformed ?: original
    }

    fun unTransform(transformed: String): String {
        val original = tagTransformerInv[transformed]
        return original ?: transformed
    }

    fun getType(term: String): TagType {
        return numberTypeInv[term]!!
    }

    fun toDataTag(tagType: TagType, tag: String): Result {
        var newTagType = tagType
        var newTag = tag
        if (tagType === TagType.TYPE || tagType === TagType.LANGUAGE) {
            newTag = transform(tag)
        } else if (tagType === TagType.TAG) {
            val len = tag.length
            val c = tag[len - 1]
            if (c == '♂') {
                newTagType = TagType.MALE
                newTag = tag.substring(0, len - 2)
            } else if (c == '♀') {
                newTagType = TagType.FEMALE
                newTag = tag.substring(0, len - 2)
            }
        }
        return Result(newTagType, newTag)
    }

    fun toOriginalDataTag(tagType: TagType, tag: String): Result {
        val result = toDataTag(tagType, tag)
        val original = toOriginalNonNull(result.tag, result.tagType)
        return Result(result.tagType, original)
    }

    fun toDisplayTag(tagType: TagType, tagList: MutableList<String>) {
        val size = tagList.size
        for (i in 0 until size) {
            tagList[i] = toDisplayTag(tagType, tagList[i]).tag
        }
    }

    fun toDisplayTag(tagType: TagType, tag: String): Result {
        var newTag = tag
        var newTagType = tagType

        if (tagType === TagType.TYPE ||
            tagType === TagType.LANGUAGE) {
            newTag = unTransform(tag)
        } else if (tagType === TagType.MALE) {
            newTag = "$tag ♂"
            newTagType = TagType.TAG
        } else if (tagType === TagType.FEMALE) {
            newTag = "$tag ♀"
            newTagType = TagType.TAG
        }
        return Result(newTagType, newTag)
    }

    data class Result(val tagType: TagType, val tag: String)

}