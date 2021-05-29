package com.vtsb.hipago.data.datasource.local.adapter

import android.util.Log
import com.google.common.collect.BiMap
import com.vtsb.hipago.data.util.TagConverter
import com.vtsb.hipago.data.datasource.local.dao.GalleryBlockDao
import com.vtsb.hipago.data.datasource.local.entity.GalleryData
import com.vtsb.hipago.data.datasource.local.entity.GalleryRelated
import com.vtsb.hipago.data.datasource.local.entity.TagData
import com.vtsb.hipago.data.datasource.local.entity.relation.GalleryDataTagDataCrossRef
import com.vtsb.hipago.data.datasource.remote.entity.GalleryBlockWithOtherData
import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.domain.entity.TagType
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.collections.HashMap

@Singleton
class GalleryBlockDaoAdapter @Inject constructor(
    private val galleryBlockDao: GalleryBlockDao,
    @Named("tagNumberBiMap") private val tagNumberBiMapArray: Array<BiMap<String, Long>>,
    private val tagConverter: TagConverter
) {

    fun getGalleryBlock(id: Int): GalleryBlockWithOtherData? {
        val fullGalleryData = galleryBlockDao.getFullGalleryData(id.toLong()) ?: return null

        val galleryData = fullGalleryData.galleryDataWithTagData.galleryData
        val tags = HashMap<TagType, List<String>>()
        for(tagData in fullGalleryData.galleryDataWithTagData.tagDataList) {
            var tagList = tags[tagData.type] as LinkedList<String>?
            if (tagList == null) {
                tagList = LinkedList()
                tags[tagData.type] = tagList
            }
            tagList.add(tagData.name)
        }

        val relatedList = LinkedList<Int>()
        for (related in fullGalleryData.related) {
            relatedList.add(related.related.toInt())
        }

        return GalleryBlockWithOtherData(GalleryBlock(
            id, galleryData.type,
            galleryData.title,
            galleryData.date,
            tags,
            galleryData.thumbnail,
            relatedList
        ), galleryData.url)
    }

    fun save(galleryBlock: GalleryBlock, detailedUrl: String) {
        val id = galleryBlock.id.toLong()
        val galleryData = GalleryData(
            id,
            galleryBlock.type,
            galleryBlock.title,
            galleryBlock.date,
            galleryBlock.thumbnail,
            detailedUrl)

        val tagList = LinkedList<GalleryDataTagDataCrossRef>()
        tagList.addAll(getGalleryDataTagDataCrossRefList(id, TagType.TYPE, galleryBlock.tags[TagType.TYPE]))
        tagList.addAll(getGalleryDataTagDataCrossRefList(id, TagType.LANGUAGE, galleryBlock.tags[TagType.LANGUAGE]))
        tagList.addAll(getGalleryDataTagDataCrossRefList(id, TagType.GROUP, galleryBlock.tags[TagType.GROUP]))
        tagList.addAll(getGalleryDataTagDataCrossRefList(id, TagType.ARTIST, galleryBlock.tags[TagType.ARTIST]))
        tagList.addAll(getGalleryDataTagDataCrossRefList(id, TagType.SERIES, galleryBlock.tags[TagType.SERIES]))
        tagList.addAll(getGalleryDataTagDataCrossRefList(id, TagType.CHARACTER, galleryBlock.tags[TagType.CHARACTER]))
        tagList.addAll(getGalleryDataTagDataCrossRefList(id, TagType.TAG, galleryBlock.tags[TagType.TAG]))

        val galleryRelatedList = LinkedList<GalleryRelated>()
        for (related in galleryBlock.related) {
            galleryRelatedList.add(GalleryRelated(null, id, related.toLong()))
        }

        galleryBlockDao.updateGalleryBlock(galleryData, tagList, galleryRelatedList)
    }

    private fun getGalleryDataTagDataCrossRefList(id: Long, tagType: TagType, tagList: List<String>?): List<GalleryDataTagDataCrossRef> {
        val tagNumberBiMap = tagNumberBiMapArray[tagType.id.toInt()]
        val results = LinkedList<GalleryDataTagDataCrossRef>()
        if (tagList == null) return results
        for (tag in tagList) {
            results.add(getGalleryDataTagDataCrossRef(id, tagType, tag, tagNumberBiMap))
        }
        return results
    }

    private fun getGalleryDataTagDataCrossRef(id: Long, tagType: TagType, tag: String, tagNumberBiMap: BiMap<String, Long>): GalleryDataTagDataCrossRef {
        val original = tagConverter.toDataTag(tagType, tag).tag

        var tagId = tagNumberBiMap[original]
        if (tagId == null) {
            tagId = galleryBlockDao.getTagNum(tagType, original)

            if (tagId == null) {
                val tagData = TagData(null, tagType, original, 1L)
                tagId = galleryBlockDao.insertEnglishTag(tagData)
                if (tagId == null) {
                    Log.e(this.javaClass.name, "number insert fail:$tagType, $tag")
                    throw IOException()
                }
            }
            tagNumberBiMap[original] = tagId
        }

        return GalleryDataTagDataCrossRef(id, tagId)
    }



}