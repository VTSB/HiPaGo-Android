package com.vtsb.hipago.data.repository

import com.vtsb.hipago.data.datasource.remote.service.original.CommonJs
import com.vtsb.hipago.data.mapper.GalleryDataServiceMapper
import com.vtsb.hipago.domain.entity.GalleryImages
import com.vtsb.hipago.domain.repository.GalleryImageRepository
import com.vtsb.hipago.util.helper.FileHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GalleryImageRepositoryImpl @Inject constructor(
    private val galleryDataServiceMapper: GalleryDataServiceMapper,
    private val fileHelper: FileHelper,
    private val commonJs: CommonJs,
) : GalleryImageRepository {

    override fun loadList(id: Int): GalleryImages =
        galleryDataServiceMapper.getGalleryImages(id)

    


}