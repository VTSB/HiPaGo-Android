package com.vtsb.hipago.data.datasource.local.dao

import androidx.room.*
import com.vtsb.hipago.data.datasource.local.entity.GalleryData
import com.vtsb.hipago.data.datasource.local.entity.relation.GalleryDataTagDataCrossRef
import com.vtsb.hipago.data.datasource.local.entity.relation.GalleryDataWithTagData
import org.json.JSONObject

@Dao
public abstract class GalleryBlockDao {

    @Transaction
    public open fun insertGalleryBlock(galleryData: GalleryData, galleryTagDataList: List<GalleryDataTagDataCrossRef>) {
        insertGalleryData(galleryData)
        insertGalleryTagDataList(galleryTagDataList)
    }

    @Transaction
    public open fun updateGalleryBlock(galleryData: GalleryData, galleryTagDataList: List<GalleryDataTagDataCrossRef>) {
        deleteGalleryTagData(galleryData.id)
        insertGalleryBlock(galleryData, galleryTagDataList)
    }

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract fun insertGalleryData(galleryData: GalleryData)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract fun insertGalleryTagDataList(galleryTagDataList: List<GalleryDataTagDataCrossRef>)

    @Transaction
    @Query("SELECT * FROM gallery_data WHERE `id` = :id;")
    public abstract fun getLocalGalleryData(id: Long): GalleryDataWithTagData?

    @Query("UPDATE gallery_data SET extraData = :extraData WHERE id = :id;")
    public abstract fun updateExtraData(id: Long, extraData: JSONObject)

    @Query("DELETE FROM gallery_data_tag_data_cross_ref WHERE `id` = :id;")
    public abstract fun deleteGalleryTagData(id: Long)

}