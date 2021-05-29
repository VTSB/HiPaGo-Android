package com.vtsb.hipago.presentation.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vtsb.hipago.databinding.FragmentGalleryListBinding
import com.vtsb.hipago.presentation.viewmodel.GalleryListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryListFragment : Fragment() {

    private val galleryListViewModel: GalleryListViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentGalleryListBinding.inflate(inflater, container, false)

        binding.viewModel = galleryListViewModel



        return binding.root
    }


}