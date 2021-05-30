package com.vtsb.hipago.data.repository

import com.vtsb.hipago.data.mapper.GalleryDataServiceMapper
import com.vtsb.hipago.domain.entity.GalleryImage
import com.vtsb.hipago.domain.entity.GalleryImages
import com.vtsb.hipago.domain.entity.ImageType
import com.vtsb.hipago.domain.repository.GalleryImageRepository
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.HashSet

@Singleton
class GalleryImageRepositoryImpl @Inject constructor(
    private val galleryDataServiceMapper: GalleryDataServiceMapper
) : GalleryImageRepository {

    override fun loadList(id: Int): GalleryImages {
        val info = galleryDataServiceMapper.getGalleryInfo(id)

        val list: MutableList<GalleryImage> = ArrayList()
        for(file in info.files) {
            val imageTypes: MutableSet<ImageType> = hashSetOf(ImageType.ORIGINAL)
            if (file.haswebp == 1) imageTypes.add(ImageType.WEBP)
            if (file.hasavif == 1) imageTypes.add(ImageType.AVIF)

            list.add(GalleryImage(file.name, file.hash, imageTypes))
        }

        return GalleryImages(id, list)
    }


}