package com.vtsb.hipago.presentation.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vtsb.hipago.databinding.FragmentGalleryDetailBinding
import com.vtsb.hipago.presentation.viewmodel.GalleryDetailViewModel
import com.vtsb.hipago.util.converter.TagConverter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GalleryDetailFragment : Fragment() {

    private val viewModel: GalleryDetailViewModel by viewModels()

    @Inject lateinit var tagConverter: TagConverter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentGalleryDetailBinding.inflate(inflater, container, false)

        val galleryInformationFragmentArgs = GalleryDetailFragmentArgs.fromBundle(arguments)

        val galleryBlock = galleryInformationFragmentArgs.galleryBlock






        return super.onCreateView(inflater, container, savedInstanceState)
    }

}