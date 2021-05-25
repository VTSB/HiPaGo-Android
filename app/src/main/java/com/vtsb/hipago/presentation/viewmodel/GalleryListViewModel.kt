package com.vtsb.hipago.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vtsb.hipago.domain.usecase.GalleryBlockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryListViewModel @Inject constructor(
    private val galleryBlockUseCase: GalleryBlockUseCase
) : ViewModel() {

    val nowPage = MutableLiveData(1)
    val maxPage = MutableLiveData(-1)





}