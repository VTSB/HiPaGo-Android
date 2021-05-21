package com.vtsb.hipago.data.datasource.local.dao

import androidx.room.*
import com.vtsb.hipago.data.datasource.local.entity.GalleryData
import com.vtsb.hipago.data.datasource.local.entity.GalleryTag
import com.vtsb.hipago.data.datasource.local.entity.relation.LocalGalleryData

@Dao
public abstract class GalleryBlockDao {

    @Transaction
    public fun insertGalleryBlock(galleryData: GalleryData, galleryTagDataList: List<GalleryTag>) {
        insertGalleryData(galleryData)
        insertGalleryTagDataList(galleryTagDataList)
    }

    @Transaction
    public fun updateGalleryBlock(galleryData: GalleryData, galleryTagDataList: List<GalleryTag>) {
        deleteGalleryTagData(galleryData.id)
        insertGalleryBlock(galleryData, galleryTagDataList)
    }



    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertGalleryData(gd: GalleryData)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertGalleryTagDataList(galleryTagDataList: List<GalleryTag>)

    @Transaction
    @Query("SELECT * FROM gallery_data WHERE `id` = :id;")
    public abstract fun getLocalGalleryData(id: Long): LocalGalleryData?

    @Update(entity = GalleryData::class)
    abstract fun updateExtraData(data: String)

    @Query("DELETE FROM gallery_tag WHERE `id` = :id;")
    abstract fun deleteGalleryTagData(id: Long)
}