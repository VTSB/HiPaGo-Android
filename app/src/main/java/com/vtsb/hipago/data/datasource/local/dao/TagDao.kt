package com.vtsb.hipago.data.datasource.local.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.vtsb.hipago.data.datasource.local.entity.LanguageTag
import com.vtsb.hipago.data.datasource.local.entity.TagData
import com.vtsb.hipago.data.datasource.local.entity.TagDataLocal
import com.vtsb.hipago.data.datasource.local.entity.TagDataTransform
import com.vtsb.hipago.data.datasource.local.entity.pojo.SuggestionLocal
import com.vtsb.hipago.data.datasource.local.entity.pojo.SuggestionOriginal
import com.vtsb.hipago.data.datasource.local.entity.pojo.TagDataWithLocal
import com.vtsb.hipago.domain.entity.TagType
import java.util.*

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

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertLanguageTags(languageTagList: List<LanguageTag>): List<Long>

    @Transaction
    open fun newInsertLanguageTags(languageTagList: List<LanguageTag>) {
        val insertResult = insertLanguageTags(languageTagList)
        val newInsertList: MutableList<LanguageTag> = ArrayList<LanguageTag>()
        for (i in languageTagList.indices) {
            if (insertResult[i] == -1L) {
                newInsertList.add(languageTagList[i])
            }
        }
        if (newInsertList.isNotEmpty()) {
            insertLanguageTags(newInsertList)
        }
    }

    fun updateEnglishTag(tagData: TagData) {
        updateEnglishTag(tagData.type, tagData.name, tagData.amount)
    }

    @Query("UPDATE `tag_data` SET amount=:amount WHERE type=:type AND name=:name;")
    abstract fun updateEnglishTag(type: TagType, name: String, amount: Long)

    @Update
    abstract fun updateEnglishTags(tagDataList: List<TagData>)

    // https://stackoverflow.com/questions/45677230/android-room-persistence-library-upsert
    @Transaction
    open fun upsertEnglishTag(tagData: TagData) {
        if (insertEnglishTag(tagData) == -1L) {
            updateEnglishTag(tagData)
        }
    }

    @Transaction
    open fun upsertEnglishTags(tagDataList: List<TagData>) {
        val insertResult = insertEnglishTags(tagDataList)
        for (i in tagDataList.indices) {
            if (insertResult[i] == -1L) {
                updateEnglishTag(tagDataList[i])
            }
        }
    }

    @Query("SELECT tag_data.`tagId` FROM tag_data WHERE tag_data.type = :type AND tag_data.name = :englishTag;")
    abstract fun getTagNum(type: TagType, englishTag: String): Long

    @get:Query("SELECT * FROM tag_data_transform;")
    abstract val allTagDataTransforms: List<TagDataTransform>

    @get:Query("SELECT * FROM language_tag;")
    abstract val allLanguageTags: List<LanguageTag>

    @Query("""SELECT tag_data.name, tag_data_local.local FROM tag_data_local
            INNER JOIN tag_data ON tag_data.`tagId` = tag_data_local.`tagId` AND tag_data.type=:tagType
            WHERE tag_data_local.language=:languageNo;""")
    abstract fun getTagDataWithLocalByType(
        tagType: TagType,
        languageNo: Long
    ): List<TagDataWithLocal>

    @Query("""SELECT tag_data_local.local FROM tag_data_local 
                INNER JOIN tag_data ON tag_data.name=:englishTag AND tag_data.type=:tagType AND tag_data.`tagId` = tag_data_local.`tagId` 
                WHERE tag_data_local.language=:languageNo LIMIT 1;""")
    abstract fun getLocalTag(tagType: TagType, englishTag: String, languageNo: Long): String

    @RawQuery(observedEntities = [TagDataLocal::class, TagData::class])
    abstract fun searchLocal(query: SupportSQLiteQuery?): List<SuggestionLocal>

    @RawQuery(observedEntities = [TagData::class])
    abstract fun searchEnglish(query: SupportSQLiteQuery?): List<SuggestionOriginal>

}