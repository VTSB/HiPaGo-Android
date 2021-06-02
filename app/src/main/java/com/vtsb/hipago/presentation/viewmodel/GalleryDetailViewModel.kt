package com.vtsb.hipago.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.domain.entity.GalleryBlockType
import com.vtsb.hipago.domain.entity.GalleryImages
import com.vtsb.hipago.domain.usecase.GalleryBlockUseCase
import com.vtsb.hipago.domain.usecase.ImageUseCase
import com.vtsb.hipago.presentation.view.custom.adapter.RecyclerViewAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.sql.Date
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class GalleryDetailViewModel @Inject constructor(
    private val galleryBlockUseCase: GalleryBlockUseCase,
    private val imageUseCase: ImageUseCase,
) : ViewModel() {

    private lateinit var galleryBlock: GalleryBlock
    private lateinit var images: SharedFlow<GalleryImages>
    private lateinit var galleryBlockList: MutableList<GalleryBlock>

    fun init(galleryBlock: GalleryBlock) {
        this.galleryBlock = galleryBlock
        this.galleryBlockList = ArrayList(galleryBlock.related.size)
        for (id in galleryBlock.related) {
            galleryBlockList.add(GalleryBlock(id, GalleryBlockType.LOADING, "", Date(0), mapOf(), "", LinkedList()))
        }
        this.images = imageUseCase.loadList(galleryBlock.id)
    }

    fun updateRelated(listener: RecyclerViewAdapter.Listener) {
        viewModelScope.launch {
            for ((idx, id) in galleryBlock.related.withIndex()) {
                galleryBlockUseCase.getGalleryBlock(id)
                    .collect {
                        galleryBlockList[idx] = it
                        viewModelScope.launch { listener.onItemChangedSync(idx) }
                    }
            }
        }
    }

    fun getGalleryBlockList(): List<GalleryBlock> = galleryBlockList

}