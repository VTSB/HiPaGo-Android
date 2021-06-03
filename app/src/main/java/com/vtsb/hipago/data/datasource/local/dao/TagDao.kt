package com.vtsb.hipago.data.datasource.local.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.vtsb.hipago.data.datasource.local.entity.TagData
import com.vtsb.hipago.data.datasource.local.entity.TagDataLocal
import com.vtsb.hipago.data.datasource.local.entity.TagDataTransform
import com.vtsb.hipago.data.datasource.local.entity.relation.LocalTagData
import com.vtsb.hipago.domain.entity.Suggestion
import com.vtsb.hipago.domain.entity.TagType

@Dao
abstract class TagDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTagDataTransforms(tagDataTransform: List<TagDataTransform>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertEnglishTag(tagData: TagData): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertEnglishTags(tagDataList: List<TagData>): List<Long>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertLocalTags(tagDataLocalList: List<TagDataLocal>)

    private fun updateEnglishTag(tagData: TagData) {
        updateEnglishTag(tagData.type, tagData.name, tagData.amount)
    }

    @Query("UPDATE `tag_data` SET amount=:amount WHERE type=:type AND name=:name;")
    abstract fun updateEnglishTag(type: TagType, name: String, amount: Int)

    @Update
    abstract fun updateEnglishTags(tagDataList: List<TagData>)

    // https://stackoverflow.com/questions/45677230/android-room-persistence-library-upsert
    @Transaction
    open fun upsertEnglishTag(tagData: TagData) {
        if (insertEnglishTag(tagData) == -1L) {
            updateEnglishTag(tagData)
        }
    }

    @Query("SELECT tag_data.`tagId` FROM tag_data WHERE tag_data.type = :type AND tag_data.name = :englishTag;")
    abstract fun getTagNum(type: TagType, englishTag: String): Long?

    @Transaction
    @Query("SELECT * FROM tag_data_transform;")
    abstract fun getAllTagDataTransform(): List<TagDataTransform>

    @Transaction
    @Query("SELECT * FROM tag_data_local;")
    abstract fun getAllLocalTagData(): List<LocalTagData>

    @Query("""SELECT tag_data_local.local FROM tag_data_local 
                INNER JOIN tag_data ON tag_data.name=:englishTag AND tag_data.type=:tagType AND tag_data.`tagId` = tag_data_local.`tagId` 
                LIMIT 1;""")
    abstract fun getLocalTag(tagType: TagType, englishTag: String): String

    @RawQuery(observedEntities = [TagDataLocal::class, TagData::class])
    abstract fun searchLocal(query: SupportSQLiteQuery): List<Suggestion>

    @RawQuery(observedEntities = [TagData::class])
    abstract fun searchEnglish(query: SupportSQLiteQuery): List<Suggestion>

}