package com.vtsb.hipago.data.initializer

import android.util.Log
import com.google.common.collect.BiMap
import com.vtsb.hipago.data.datasource.local.dao.InitializeDao
import com.vtsb.hipago.data.datasource.local.dao.TagDao
import com.vtsb.hipago.data.datasource.local.entity.InitializeLog
import com.vtsb.hipago.data.datasource.local.entity.TagData
import com.vtsb.hipago.data.datasource.local.entity.TagDataLocal
import com.vtsb.hipago.data.datasource.local.entity.TagDataTransform
import com.vtsb.hipago.data.datasource.memory.LocalizationGetter
import com.vtsb.hipago.data.datasource.memory.TagTransformGetter
import com.vtsb.hipago.data.datasource.remote.entity.TagWithAmount
import com.vtsb.hipago.data.mapper.GalleryDataServiceMapper
import com.vtsb.hipago.data.mapper.GalleryServiceMapper
import com.vtsb.hipago.domain.entity.TagType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Named

class DatabaseInitializer @Inject constructor(
    private val localizationGetter: LocalizationGetter,
    private val tagTransformGetter: TagTransformGetter,
    private val initializeDao: InitializeDao,
    private val tagDao: TagDao,
    private val galleryDataServiceMapper: GalleryDataServiceMapper,
    private val galleryServiceMapper: GalleryServiceMapper,
    @Named("tagTransformerBiMap") private val tagTransformerBiMap: BiMap<String, String>,
    @Named("stringTypeBiMap") private val stringType: BiMap<String, TagType>,
    @Named("tagLocalizationBiMap") private val tagLocalization: Array<BiMap<String, String>>,
    @Named("localizationLanguageMSF") private val localizationLanguageMSF: MutableStateFlow<String>,
    private val initializationStatus: InitializationStatus,
) {

    companion object {
        private const val TAG_MODE_TYPE = "m_type"
        private const val TAG_MODE_LANGUAGE = "m_lang"
        private const val TAG_MODE_ENGLISH = "m_eng"
        private const val TAG_MODE_TEMP_ENGLISH = "t_eng"
        private const val TAG_MODE_LOCALIZATION = "m_loc"

        private const val DATA_MODE_LOADING = "loading"
        private const val DATA_MODE_COMPLETED = "completed"

        private const val SPLITTER = ":"
    }

    suspend fun init() {

        when (initializeDao.getLog(TAG_MODE_TYPE)) {
            null->initTagTypeFirst()
        }

        when (initializeDao.getLog(TAG_MODE_LOCALIZATION)) {
            null-> initLocalizationFirst()
            DATA_MODE_COMPLETED-> initLocalizationCompleted()
        }
        initializationStatus.completeLocalization()

        when (initializeDao.getLog(TAG_MODE_LANGUAGE)) {
            null-> initLanguageFirst()
            DATA_MODE_COMPLETED-> initLanguageCompleted()
        }

        if (when (initializeDao.getLog(TAG_MODE_ENGLISH)) {
            null-> initEnglishFirst()
            DATA_MODE_LOADING-> initEnglishLoading()
            DATA_MODE_COMPLETED-> true
            else-> false }
        ) {
            initializationStatus.completeTag()
        }

    }

    private fun initTagTypeFirst() {
        val tagDataList: MutableList<TagData> = ArrayList()
        for (value in stringType.values) {
            if (value.id.toInt() == 0) continue
            tagDataList.add(TagData(null, TagType.BEFORE, value.otherName, 0))
        }
        initializeDao.initTagType(tagDataList,
            InitializeLog(TAG_MODE_TYPE, DATA_MODE_COMPLETED))
    }

    private fun initLocalizationFirst() {
        val map = localizationGetter.get(localizationLanguageMSF.value)

        var sum = 0
        for (value in map.values) sum += value.size

        val tagDataLocalList: MutableList<TagDataLocal> = ArrayList(sum)
        for ((tagType, value) in map.entries) {
            val tagTypeId = tagType.id.toInt().coerceAtMost(7)

            for ((original, local) in value.entries) {
                tagLocalization[tagTypeId][original] = local

                var tagId = tagDao.getTagNum(tagType, original)
                if (tagId == null) {
                    val tagData = TagData(null, tagType, original, 0)
                    tagId = tagDao.insertEnglishTag(tagData)
                }

                tagDataLocalList.add(TagDataLocal(tagId, local))
            }
        }

        initializeDao.initLocalizationFirst(tagDataLocalList,
            InitializeLog(TAG_MODE_LOCALIZATION, DATA_MODE_COMPLETED))
    }

    private fun initLocalizationCompleted() {
        val localTagDataList = tagDao.getAllLocalTagData()
        for (localTagData in localTagDataList) {
            tagLocalization[localTagData.tagData.type.id.toInt().coerceAtMost(7)][localTagData.tagData.name] =
                localTagData.tagDataLocal.local
        }
    }

    private suspend fun initLanguageFirst() {
        val languages = tagTransformGetter.getLanguages()

        val tagDataTransformList: MutableList<TagDataTransform> = ArrayList(tagTransformerBiMap.entries.size)
        val tagDataList: MutableList<TagData> = ArrayList(languages.size)

        for ((key, value) in tagTransformerBiMap.entries) {
            tagDataTransformList.add(TagDataTransform(key, value))
        }

        for (key in languages.keys) {
            tagDataList.add(TagData(null, TagType.LANGUAGE, key, 0))
        }

        val languageTagList = galleryDataServiceMapper.getAllLanguageTags()
        val deferredList: MutableList<Deferred<Pair<TagDataTransform, TagData>>> = ArrayList(languageTagList.size)

        coroutineScope {
            for(languageTag in languageTagList) {
                tagTransformerBiMap[languageTag.name] = languageTag.local
                deferredList.add(async {
                    val amount = galleryDataServiceMapper.getLanguageTagAmount(languageTag.name)
                    val tagDataTransform = TagDataTransform(languageTag.name, languageTag.local)
                    val tagData = TagData(null, TagType.LANGUAGE, languageTag.name, amount)
                    return@async Pair(tagDataTransform, tagData)
                })
            }
        }

        for (deferred in deferredList) {
            val result = deferred.await()
            tagDataTransformList.add(result.first)
            tagDataList.add(result.second)
        }

        initializeDao.initLanguageFirst(tagDataTransformList, tagDataList, InitializeLog(TAG_MODE_LANGUAGE, DATA_MODE_COMPLETED))
    }

    private fun initLanguageCompleted() {
        val tagDataTransformList: List<TagDataTransform> = tagDao.getAllTagDataTransform()

        for ((original, transformed) in tagDataTransformList) {
            tagTransformerBiMap[original] = transformed
        }
    }

    private fun initEnglishFirst(): Boolean {

        val tagURLListInitializeLog: MutableList<InitializeLog> = ArrayList()
        val tagDataList: MutableList<TagData> = ArrayList()

        var count = 1
        for (type in arrayOf("type", "group", "artist", "series", "character", "tag")) {
            val searchType = if (type[type.length - 1] == 's') type else type + "s"
            val result = try {
                galleryServiceMapper.getAllTagAndAllURL(searchType)
            } catch (t: Throwable) {
                return false
            }

            val tagType = stringType[type]!!
            val newTagDataList = toTagDataList(result.second, tagType) ?: return false

            tagDataList.addAll(newTagDataList)

            for (url in result.first) {
                tagURLListInitializeLog.add(InitializeLog("TAG_MODE_TEMP_ENGLISH$count", "$type$SPLITTER$url"))
                count++
            }
        }

        tagURLListInitializeLog.add(InitializeLog(TAG_MODE_ENGLISH, "$count"))
        initializeDao.initEnglishFirst(tagDataList, tagURLListInitializeLog)

        tagURLListInitializeLog.removeLast()

        return initEnglishLoading(tagURLListInitializeLog)
    }

    private fun initEnglishLoading(initializeLogList: List<InitializeLog> =
           initializeDao.getLogListLike(TAG_MODE_TEMP_ENGLISH)): Boolean {

        // real tag load start
        for ((tag, data) in initializeLogList) {
            val str = data.split(SPLITTER)
            val mode: TagType = stringType[str[0]]!!
            val url = str[1]
            val tagWithAmountList = try {
                galleryServiceMapper.getAllTag(url)
            } catch (e: Exception) {
                return false
            }
            val tagDataList = toTagDataList(tagWithAmountList, mode) ?: return false
            initializeDao.initEnglishLoading(tagDataList, tag)
        }

        initializeDao.insertLog(InitializeLog(TAG_MODE_ENGLISH, DATA_MODE_COMPLETED))
        return true
    }


    private fun toTagDataList(tagWithAmountList: List<TagWithAmount>, mode: TagType): List<TagData>? {
        val tagDataList: MutableList<TagData> = java.util.ArrayList()
        for (tagWithAmount in tagWithAmountList) {
            var name: String = tagWithAmount.tag
            val amount: Int = tagWithAmount.amount
            var type: TagType? = mode
            if (name.contains(":")) {
                val result = name.split(":").toTypedArray()
                val newType: TagType? = stringType[result[0]]
                if (newType == null) {
                    Log.e(this.javaClass.simpleName, "type_ is null. (${result.joinToString(", ")})")
                    return null
                }
                type = newType
                name = result[1]
            }
            tagDataList.add(TagData(null, type!!, name, amount))
        }
        return tagDataList
    }


}