package com.vtsb.hipago.data.datasource.local.dao

import androidx.room.*
import com.vtsb.hipago.data.datasource.local.entity.GalleryData
import com.vtsb.hipago.data.datasource.local.entity.GalleryRelated
import com.vtsb.hipago.data.datasource.local.entity.TagData
import com.vtsb.hipago.data.datasource.local.entity.relation.FullGalleryData
import com.vtsb.hipago.data.datasource.local.entity.relation.GalleryDataTagDataCrossRef
import com.vtsb.hipago.domain.entity.TagType

@Dao
abstract class GalleryBlockDao {

    @Transaction
    open fun insertGalleryBlock(galleryData: GalleryData, galleryTagDataList: List<GalleryDataTagDataCrossRef>, galleryRelatedList: List<GalleryRelated>) {
        insertGalleryData(galleryData)
        insertGalleryTagDataList(galleryTagDataList)
        if (galleryRelatedList.isNotEmpty()) insertGalleryRelatedList(galleryRelatedList)
    }

    @Transaction
    open fun updateGalleryBlock(galleryData: GalleryData, galleryTagDataList: List<GalleryDataTagDataCrossRef>, galleryRelatedList: List<GalleryRelated>) {
        deleteGalleryTagData(galleryData.id)
        deleteGalleryRelated(galleryData.id)
        insertGalleryBlock(galleryData, galleryTagDataList, galleryRelatedList)
    }

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertGalleryData(galleryData: GalleryData)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertGalleryTagDataList(galleryTagDataList: List<GalleryDataTagDataCrossRef>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertGalleryRelatedList(galleryRelatedList: List<GalleryRelated>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertEnglishTag(tagData: TagData): Long?

    @Query("SELECT tag_data.`tagId` FROM tag_data WHERE tag_data.type = :type AND tag_data.name = :englishTag LIMIT 1;")
    abstract fun getTagNum(type: TagType, englishTag: String): Long?

    @Transaction
    @Query("SELECT * FROM gallery_data WHERE `id` = :id;")
    abstract fun getFullGalleryData(id: Long): FullGalleryData?

    @Query("DELETE FROM gallery_related WHERE gallery_related.`id` = :id")
    abstract fun deleteGalleryRelated(id: Long)

    //@Query("UPDATE gallery_data SET extraData = :extraData WHERE id = :id;")
    //public abstract fun updateExtraData(id: Long, extraData: JSONObject)

    @Query("DELETE FROM gallery_data_tag_data_cross_ref WHERE `id` = :id;")
    abstract fun deleteGalleryTagData(id: Long)

}