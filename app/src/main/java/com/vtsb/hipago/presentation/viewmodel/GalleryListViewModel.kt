package com.vtsb.hipago.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.domain.entity.NumberLoadMode
import com.vtsb.hipago.domain.usecase.GalleryBlockUseCase
import com.vtsb.hipago.presentation.view.adapter.RecyclerViewAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class GalleryListViewModel @Inject constructor(
    private val galleryBlockUseCase: GalleryBlockUseCase,
    @Named("useLanguageMSF") private val useLanguageMSF: MutableStateFlow<String>,
) : ViewModel() {

    val galleryBlockList: MutableList<GalleryBlock> = ArrayList()
    val galleryNumberPageMap: MutableMap<Int, Int> = ConcurrentHashMap()
    val pagePositionHashMap: MutableMap<Int, Int> = ConcurrentHashMap()

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



    // call only once
    fun init(query: String, listener: RecyclerViewAdapter.Listener) {

        setQuery(query)
        this.listener = listener

    }



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
            resetValues()
        }
    }

    // todo: implement real load functions



    private fun setQuery(query: String) {
        val result = galleryBlockUseCase.getLoadModeFromQuery(query)
        this.loadMode = result.first
        this.query = result.second
    }

    private fun resetValues() {
        this.listener.onRangeRemovedSync(0, galleryBlockList.size)
        // this.disposable.clear()

        this.galleryNumberPageMap.clear()
        this.pagePositionHashMap.clear()
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


}
