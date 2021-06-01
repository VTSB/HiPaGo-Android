package com.vtsb.hipago.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.domain.entity.GalleryImages
import com.vtsb.hipago.domain.usecase.GalleryBlockUseCase
import com.vtsb.hipago.domain.usecase.ImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
class GalleryDetailViewModel @Inject constructor(
    private val galleryBlockUseCase: GalleryBlockUseCase,
    private val imageUseCase: ImageUseCase,
) : ViewModel() {

    private val relatedListMutableLiveData = MutableLiveData<List<GalleryBlock>>()
    private lateinit var galleryBlock: GalleryBlock
    private lateinit var images: SharedFlow<GalleryImages>

    fun init(galleryBlock: GalleryBlock) {
        this.galleryBlock = galleryBlock
        this.images = imageUseCase.loadList(galleryBlock.id)
    }

    fun getRelated()

}