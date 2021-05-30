package com.vtsb.hipago.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.vtsb.hipago.domain.usecase.GalleryBlockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryDetailViewModel @Inject constructor(
    private val galleryBlockUseCase: GalleryBlockUseCase,
) : ViewModel() {




}