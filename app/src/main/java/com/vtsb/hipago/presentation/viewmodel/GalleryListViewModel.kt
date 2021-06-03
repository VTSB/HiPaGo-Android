package com.vtsb.hipago.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
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
import com.vtsb.hipago.util.Constants
import com.vtsb.hipago.util.Constants.PAGE_SIZE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
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
    private var application: Application?,
    @Named("useLanguageMSF") private val useLanguageMSF: MutableStateFlow<String>,
) : ViewModel() {

    private val galleryBlockList: MutableList<GalleryBlock> = ArrayList()
    private val galleryIdPageMap: MutableMap<Int, Int> = ConcurrentHashMap()
    private val pagePositionMap: MutableMap<Int, Int> = ConcurrentHashMap()

    private lateinit var listener: RecyclerViewAdapter.Listener
    private lateinit var loadMode: NumberLoadMode
    private lateinit var query: String
    private lateinit var searchQuery: String
    private var language: String = useLanguageMSF.value

    private var tPage = 0
    private var bPage = 1
    private var offset = 0

    private val loadStatus = MutableLiveData(0)
    private val nowPage = MutableLiveData(1)
    private val maxPage = MutableLiveData(-1)
    private var contentRange = -1

    private var nowRunningJob: Job? = null
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
            galleryBlockList.add(getSplitter(application!!.resources.getString(R.string.gallery)))
            galleryBlockList.add(GalleryBlock(id, GalleryBlockType.LOADING, "", Date(0), mapOf(), "", LinkedList()))
            galleryBlockList.add(getSplitter(application!!.resources.getString(R.string.search_word)))
            application = null
            galleryIdPageMap[0] = 1
            galleryIdPageMap[id] = 1
            offset = 3
            galleryBlockList.addAll(galleryBlockList)
            listener.onRangeInsertedSync(0, offset)
            viewModelScope.launch {
                reloadGalleryBlock(id, 1, 1)
            }

        } catch (ignored: NumberFormatException) {
            offset = 0
        }
        loadPageInitially()
    }

    fun setQuery(query: String) {
        val result = galleryBlockUseCase.getLoadModeFromQuery(query)
        this.loadMode = result.first
        this.searchQuery = result.second
        this.query = query
        Log.d("test", "setQuery ${result.first}, ${result.second}, $query")
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
        loadPageInitially()
    }

    fun changeLoadMode(loadMode: String) {
        if (this.query != loadMode || (this.query.isEmpty() && loadMode != "index")) {
            setQuery(loadMode)
            resetValues()
            loadPageInitially()
        }
    }

    fun changeLanguage(language: String) {
        if (this.language != language) {
            galleryBlockUseCase.clearGalleryNumberBuffer(this.searchQuery, this.language)
            this.language = language
            this.useLanguageMSF.compareAndSet(language, language)
            resetValues()
            loadPageInitially()
        }
    }

    private fun loadPageInitially() {
        loadStatus.value = 0
        loadBottomPage()
    }

    fun onScroll(topPos: Int, bottomPos: Int) {
        val firstGalleryBlock: GalleryBlock = galleryBlockList[topPos]
        val firstPage: Int = galleryIdPageMap[firstGalleryBlock.id] ?: return
        nowPage.value = firstPage

        if (firstPage <= tPage + Constants.PREFETCH_PAGE) {
            loadTopPage()
        }

        // bottom
        val lastGalleryBlock: GalleryBlock = galleryBlockList[bottomPos]
        val lastPage: Int = galleryIdPageMap[lastGalleryBlock.id] ?: return

        if (lastPage >= bPage - Constants.PREFETCH_PAGE) {
            loadBottomPage()
        }
    }

    private fun loadTopPage() {
        if (nowRunningJob?.isCompleted == false || tPage < 1) return

        val displayPage = tPage--
        loadPage(displayPage, callback = { resultList ->
            synchronized(pagePositionMap) {
                pagePositionMap[displayPage] = 0
                val to = bPage
                for (i in displayPage + 1 until to) {
                    val position: Int = pagePositionMap[i] ?: error("loadTopPageSeparated pagePositionHashMap position null:$i")
                    pagePositionMap[i] = position + resultList.size
                }
                galleryBlockList.addAll(offset, resultList)
            }
            viewModelScope.launch {
                listener.onRangeInsertedSync(offset, resultList.size)
            }
        })
    }

    private fun loadBottomPage() {
        if (nowRunningJob?.isCompleted == false || (maxPage.value != -1 && bPage > maxPage.value!!)) return

        val displayPage = bPage++
        loadPage(displayPage, callback = { resultList ->
            synchronized(pagePositionMap) {
                pagePositionMap[displayPage] = galleryBlockList.size
                galleryBlockList.addAll(resultList)
            }
            viewModelScope.launch {
                listener.onRangeInsertedSync(galleryBlockList.size, resultList.size)
            }
        })
    }

    private fun loadPage(displayPage: Int, callback: (List<GalleryBlock>) -> Unit) {
        //isLoading = true
        Log.d("test", "loadPage $displayPage")
        nowRunningJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val loadPage = displayPage - 1;
                val galleryNumber = galleryBlockUseCase.getGalleryNumberListByPage(
                    loadMode,
                    searchQuery,
                    language,
                    loadPage,
                    contentRange == -1)

                val idList = galleryNumber.idList

                // todo: apply filter
                // bla bla~~

                viewModelScope.launch {
                    if (loadStatus.value == 0) loadStatus.value = 1
                    if (galleryNumber.length != 0) setContentRange(galleryNumber.length)
                }

                val galleryBlockList = LinkedList<GalleryBlock>()
                for (id in idList) {
                    galleryBlockList.add(GalleryBlock(id, GalleryBlockType.LOADING, "", Date(0), mapOf(), "", LinkedList()))
                    galleryIdPageMap[id] = displayPage
                }

                callback.invoke(galleryBlockList)

                for ((from, id) in idList.withIndex()) {
                    reloadGalleryBlock(id, displayPage, from)
                }
            } catch (t: Throwable) {
                Log.e("LoadPage", "failed $displayPage", t)
                viewModelScope.launch {
                    loadStatus.value = -1
                }
            }
        }


    }

    fun reloadGalleryBlock(id: Int, position:Int) {
        val displayPage = galleryIdPageMap[id]!!
        val offset = position - displayPage * PAGE_SIZE - offset
        viewModelScope.launch {
            reloadGalleryBlock(id, displayPage, offset)
        }
    }

    private suspend fun reloadGalleryBlock(id: Int, displayPage: Int, offset: Int, skipDB: Boolean = false) {
        CoroutineScope(Dispatchers.IO).launch {
            val flow = galleryBlockUseCase.getGalleryBlock(id, save = true, skipDB)
            flow.collect { galleryBlock ->
                // todo : filter

                pagePositionMap[displayPage]?.let { it ->
                    val nowPosition = it + offset
                    galleryBlockList[nowPosition] = galleryBlock
                    viewModelScope.launch {
                        listener.onItemChangedSync(nowPosition)
                    }
                }
            }
            flow.onCompletion { Log.d("test", "complete $id") }
        }
    }

    private fun resetValues() {
        this.listener.onRangeRemovedSync(0, galleryBlockList.size)
        this.nowRunningJob?.cancel()
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
    fun getPagePositionMap(): Map<Int, Int> = pagePositionMap
    fun getBPage(): Int = bPage
    fun getTPage(): Int = tPage
    fun getQuery(): String = query
    fun getLanguage(): String = language
    fun getLoadStatus(): LiveData<Int> = loadStatus
    fun getMaxPage(): LiveData<Int> = maxPage
    fun getNowPage(): LiveData<Int> = nowPage

}
