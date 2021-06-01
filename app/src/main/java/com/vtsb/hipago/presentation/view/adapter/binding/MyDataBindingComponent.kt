package com.vtsb.hipago.presentation.view.adapter.binding

import androidx.databinding.DataBindingComponent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyDataBindingComponent @Inject constructor(
    private val galleryDetailBindingComponent: GalleryDetailBindingComponent
) : DataBindingComponent {

    override fun getGalleryDetailBindingComponent(): GalleryDetailBindingComponent =
        galleryDetailBindingComponent


}