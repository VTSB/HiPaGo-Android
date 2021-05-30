package com.vtsb.hipago.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtsb.hipago.R
import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.domain.entity.GalleryBlockType
import com.vtsb.hipago.domain.entity.NumberLoadMode
import com.vtsb.hipago.domain.entity.Suggestion
import com.vtsb.hipago.domain.usecase.GalleryBlockUseCase
import com.vtsb.hipago.domain.usecase.SearchUseCase
import com.vtsb.hipago.presentation.view.adapter.SearchCursorAdapter
import com.vtsb.hipago.presentation.view.custom.adapter.RecyclerViewAdapter
import com.vtsb.hipago.util.Constants.PAGE_SIZE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.sql.Date
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList

@HiltViewModel
class GalleryListViewModel @Inject constructor(
    private val galleryBlockUseCase: GalleryBlockUseCase,
    private val searchUseCase: SearchUseCase,
    private val application: Application,
    @Named("useLanguageMSF") private val useLanguageMSF: MutableStateFlow<String>,
) : ViewModel() {

    private val galleryBlockList: MutableList<GalleryBlock> = ArrayList()
    private val galleryIdPageMap: MutableMap<Int, Int> = ConcurrentHashMap()
    private val pagePositionMap: MutableMap<Int, Int> = ConcurrentHashMap()

    private lateinit var listener: RecyclerViewAdapter.Listener
    private lateinit var loadMode: NumberLoadMode
    private lateinit var query: String
    private var language: String = useLanguageMSF.value

    private var tPage = 0
    private var bPage = 1
    private var offset = 0



    val nowPage = MutableLiveData(1)
    val maxPage = MutableLiveData(-1)
    private var contentRange = -1

    private var isLoading = false
    private var haveInit = false

    val searchResultGetter = object: SearchCursorAdapter.SearchResultGetter {
            override fun getSuggestions(query: String): List<Suggestion> =
                searchUseCase.getSuggestions(query)}


    // call only once
    fun init(listener: RecyclerViewAdapter.Listener) {
        if(haveInit) return
        haveInit = true
        this.listener = listener

        try {
            val id = query.toInt()
            val galleryBlockList: MutableList<GalleryBlock> = java.util.ArrayList()
            galleryBlockList.add(getSplitter(application.resources.getString(R.string.gallery)))
            galleryBlockList.add(GalleryBlock(id, GalleryBlockType.LOADING, "", Date(0), mapOf(), "", LinkedList()))
            galleryBlockList.add(getSplitter(application.resources.getString(R.string.search_word)))
            galleryIdPageMap[0] = 1
            galleryIdPageMap[id] = 1
            offset = 3
            galleryBlockList.addAll(galleryBlockList)
            listener.onRangeInsertedSync(0, offset)
            reloadGalleryBlock(id, 1)

        } catch (ignored: NumberFormatException) {
            offset = 0
        }
        loadBottomPage()
    }

    fun setQuery(query: String) {
        val result = galleryBlockUseCase.getLoadModeFromQuery(query)
        this.loadMode = result.first
        this.query = result.second
    }

    private fun getSplitter(title: String): GalleryBlock =
        GalleryBlock(0, GalleryBlockType.LOADING, title, Date(0), mapOf(), "", LinkedList())


    fun changePage(page: Int) {
        val tempMaxPage = this.maxPage.value
        val tempContentRange = this.contentRange

        resetValues()
        setPage(page)
        this.maxPage.value = tempMaxPage
        this.contentRange = tempContentRange
    }

    fun changeLoadMode(loadMode: String) {
        if (this.query != loadMode) {
            setQuery(query)
            resetValues()
        }
    }

    fun changeLanguage(language: String) {
        if (this.language != language) {
            galleryBlockUseCase.clearGalleryNumberBuffer(this.query, this.language)
            this.language = language
            this.useLanguageMSF.compareAndSet(language, language)
            resetValues()
        }
    }

    fun loadTopPage() {
        if (isLoading || tPage < 1) return

        val displayPage = tPage--
        loadPage(
            displayPage, callback = { resultList ->
                synchronized(pagePositionMap) {
                    pagePositionMap[displayPage] = 0
                    val to = bPage
                    for (i in displayPage + 1 until to) {
                        val position: Int = pagePositionMap[i] ?: error("loadTopPageSeparated pagePositionHashMap position null:$i")
                        pagePositionMap[i] = position + resultList.size
                    }
                    galleryBlockList.addAll(offset, galleryBlockList)
                }
                viewModelScope.launch {
                    listener.onRangeInsertedSync(offset, resultList.size) }
            })
    }

    fun loadBottomPage() {
        if (isLoading || (maxPage.value != -1 && bPage > maxPage.value!!)) return

        val displayPage = bPage++
        loadPage(displayPage, callback = { resultList ->
            val galleryArrayListSize: Int = galleryBlockList.size
            synchronized(pagePositionMap) {
                pagePositionMap[displayPage] = galleryArrayListSize
                galleryBlockList.addAll(resultList)
            }
            viewModelScope.launch {
                listener.onRangeInsertedSync(galleryArrayListSize, resultList.size) }
        })
    }

    fun loadPage(displayPage: Int, callback: (List<GalleryBlock>) -> Unit) {
        isLoading = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val loadPage = displayPage - 1;
                val galleryNumber = galleryBlockUseCase.getGalleryNumberListByPage(
                    loadMode,
                    query,
                    language,
                    loadPage,
                    contentRange == -1
                )

                //
                val idList = galleryNumber.numberList

                // todo: apply filter
                // bla bla~~


                if (galleryNumber.length != 0) {
                    setContentRange(galleryNumber.length)
                }

                val galleryBlockList = LinkedList<GalleryBlock>()
                for (id in idList) {
                    galleryBlockList.add(GalleryBlock(id, GalleryBlockType.LOADING, "", Date(0), mapOf(), "", LinkedList()))
                    galleryIdPageMap[id] = displayPage
                }

                callback.invoke(galleryBlockList)

                for (id in idList) {
                    reloadGalleryBlock(id, displayPage)
                }
            } finally {
                isLoading = false
            }
        }


    }

    fun reloadGalleryBlock(id: Int, displayPage: Int, skipDB: Boolean = false) {
        CoroutineScope(Dispatchers.IO).launch {
            galleryBlockUseCase.getGalleryBlock(id, save = true, skipDB)
                .collect { galleryBlock ->
                    // todo : filter

                    pagePositionMap[displayPage]?.let { nowPosition ->
                        synchronized(nowPosition) {
                            galleryBlockList[nowPosition] = galleryBlock
                            viewModelScope.launch {
                                listener.onItemChangedSync(nowPosition)
                            }
                        }
                    }
                }
        }
    }




    private fun resetValues() {
        this.listener.onRangeRemovedSync(0, galleryBlockList.size)
        // this.disposable.clear()

        this.galleryIdPageMap.clear()
        this.pagePositionMap.clear()
        this.galleryBlockList.clear()
        this.setPage(1)

        this.nowPage.value = 1
        this.maxPage.value = -1
        this.contentRange = -1
    }

    private fun setPage(page: Int) {
        bPage = page
        tPage = page - 1
    }

    private fun setContentRange(length: Int) {
        contentRange = length
        maxPage.value = length / PAGE_SIZE + if (contentRange % PAGE_SIZE != 0) 1 else 0
    }

    fun getGalleryBlockList(): List<GalleryBlock> = galleryBlockList
    fun getGalleryIdPageMap(): Map<Int, Int> = galleryIdPageMap
    fun getPagePositionMap(): Map<Int, Int> = pagePositionMap
    fun getBPage(): Int = bPage
    fun getTPage(): Int = tPage
    fun getQuery(): String = query
    fun getLanguage(): String = language

}
