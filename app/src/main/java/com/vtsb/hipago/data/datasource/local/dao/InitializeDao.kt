package com.vtsb.hipago.data.datasource.local.dao

import androidx.room.*
import com.vtsb.hipago.data.datasource.local.entity.InitializeLog
import com.vtsb.hipago.data.datasource.local.entity.TagData
import com.vtsb.hipago.data.datasource.local.entity.TagDataLocal
import com.vtsb.hipago.data.datasource.local.entity.TagDataTransform
import com.vtsb.hipago.domain.entity.TagType
import java.util.*

@Dao
abstract class InitializeDao {

    // basic functions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertLog(initializeLog: InitializeLog)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertLogList(initializeLogList: List<InitializeLog>)

    @Query("DELETE FROM initialize_log WHERE initialize_log.`tag` = :tag")
    abstract fun deleteLog(tag: String)

    @Query("SELECT initialize_log.`data` FROM initialize_log WHERE `tag`=:tag;")
    abstract fun getLog(tag: String): String?

    // https://stackoverflow.com/questions/44184769/android-room-select-query-with-like
    @Query("SELECT * FROM initialize_log WHERE `tag` LIKE :tag || '%';")
    abstract fun getLogListLike(tag: String): List<InitializeLog>

    /////////////////////////////////////////

    @Transaction
    open fun initTagType(tagDataList: List<TagData>, initializeLog: InitializeLog) {
        upsertEnglishTags(tagDataList)
        insertLog(initializeLog)
    }

    @Transaction
    open fun initLocalizationFirst(tagDataLocalList: List<TagDataLocal>, initializeLog: InitializeLog) {
        insertLocalTags(tagDataLocalList)
        insertLog(initializeLog)
    }

    @Transaction
    open fun initLanguageFirst(tagDataTransformList: List<TagDataTransform>, tagDataList: List<TagData>, initializeLog: InitializeLog) {
        insertTagDataTransforms(tagDataTransformList)
        upsertEnglishTags(tagDataList)
        insertLog(initializeLog)
    }

    @Transaction
    open fun initEnglishFirst(tagDataList: List<TagData>, initializeLogList: List<InitializeLog>) {
        upsertEnglishTags(tagDataList)
        insertLogList(initializeLogList)
    }

    @Transaction
    open fun initEnglishLoading(tagDataList: List<TagData>, tag: String) {
        upsertEnglishTags(tagDataList)
        deleteLog(tag)
    }

    /////////////////////////////////////////
    @Transaction
    open fun upsertEnglishTags(tagDataList: List<TagData>) {
        val insertResult = insertEnglishTags(tagDataList)
        for (i in tagDataList.indices) {
            if (insertResult[i] == -1L) {
                val tagData = tagDataList[i]
                updateEnglishTag(tagData.type, tagData.name, tagData.amount)
            }
        }
    }

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTagDataTransforms(tagDataTransform: List<TagDataTransform>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertLocalTags(tagDataLocalList: List<TagDataLocal>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertEnglishTags(tagDataList: List<TagData>): List<Long>

    @Query("UPDATE `tag_data` SET amount=:amount WHERE type=:type AND name=:name;")
    abstract fun updateEnglishTag(type: TagType, name: String, amount: Int)

}