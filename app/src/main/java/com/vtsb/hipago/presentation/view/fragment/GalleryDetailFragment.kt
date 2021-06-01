package com.vtsb.hipago.presentation.view.fragment

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.vtsb.hipago.R
import com.vtsb.hipago.databinding.FragmentGalleryDetailBinding
import com.vtsb.hipago.domain.entity.GalleryBlockType
import com.vtsb.hipago.presentation.view.MainActivity
import com.vtsb.hipago.presentation.view.adapter.GalleryListAdapter
import com.vtsb.hipago.presentation.view.adapter.binding.MyDataBindingComponent
import com.vtsb.hipago.presentation.view.custom.listener.RecyclerItemClickListener
import com.vtsb.hipago.presentation.viewmodel.GalleryDetailViewModel
import com.vtsb.hipago.util.converter.TagConverter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GalleryDetailFragment : Fragment() {

    private val viewModel: GalleryDetailViewModel by viewModels()

    @Inject lateinit var bindingComponent: MyDataBindingComponent
    @Inject lateinit var tagConverter: TagConverter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<FragmentGalleryDetailBinding>(inflater, R.layout.fragment_gallery_detail, container, false, bindingComponent)

        val galleryInformationFragmentArgs = GalleryDetailFragmentArgs.fromBundle(requireArguments())
        val galleryBlock = galleryInformationFragmentArgs.galleryBlock
        viewModel.init(galleryBlock)

        val adapter = GalleryListAdapter(viewModel.getGalleryBlockList())
        viewModel.updateRelated(adapter.listener)

        binding.related.adapter = adapter
        binding.related.addOnItemTouchListener(RecyclerItemClickListener(requireContext(), object: RecyclerItemClickListener.OnItemClickListener.Normal.Builder() {
            override fun onItemClick(view: View, position: Int) {
                val related = viewModel.getGalleryBlockList()[position]
                when (related.type) {
                    GalleryBlockType.MI_DETAILED, GalleryBlockType.MI_NOT_DETAILED-> {
                        Navigation.findNavController(view).navigate(
                            GalleryDetailFragmentDirections
                                .actionGalleryDetailFragmentSelf(related)
                        )
                    }
                    else-> {}
                }
            }
        }))

        binding.model = galleryBlock
        binding.lifecycleOwner = viewLifecycleOwner
        binding.read.setOnClickListener { TODO("Move to Reader Fragment") }
        binding.download.setOnClickListener { TODO("Make download function") }

        setHasOptionsMenu(true)
        val activity = requireActivity() as MainActivity
        activity.setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitle(R.string.detail_view)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.gallery_info, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            requireActivity().onBackPressed()
        }
        return false
    }

}