package com.vtsb.hipago.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vtsb.hipago.databinding.ItemGalleryBlockType1Binding
import com.vtsb.hipago.databinding.ItemGalleryBlockType2Binding
import com.vtsb.hipago.databinding.ItemSplitterBinding
import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.presentation.view.custom.adapter.RecyclerViewAdapter
import com.vtsb.hipago.presentation.viewmodel.GalleryListViewModel

class GalleryListAdapter constructor(
    galleryListViewModel: GalleryListViewModel
) : RecyclerViewAdapter<RecyclerView.ViewHolder>() {

    private val nowViewType = 1
    private val galleryBlockList: List<GalleryBlock> = galleryListViewModel.getGalleryBlockList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0-> SplitterViewHolder(ItemSplitterBinding.inflate(inflater, parent, false))
            1-> GalleryBlockViewHolder1(ItemGalleryBlockType1Binding.inflate(inflater, parent, false))
            2-> GalleryBlockViewHolder2(ItemGalleryBlockType2Binding.inflate(inflater, parent, false))
            else-> GalleryBlockViewHolder1(ItemGalleryBlockType1Binding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val gb = galleryBlockList[position]
        when (getItemViewType(gb)) {
            0-> (holder as SplitterViewHolder).bind(gb)
            1-> (holder as GalleryBlockViewHolder1).bind(gb)
            2-> (holder as GalleryBlockViewHolder2).bind(gb)
            else-> (holder as GalleryBlockViewHolder1).bind(gb)
        }
    }

    override fun getItemId(position: Int): Long {
        return galleryBlockList[position].hashCode().toLong()
    }

    override fun getItemCount(): Int =
        galleryBlockList.size

    override fun getItemViewType(position: Int): Int {
        return getItemViewType(galleryBlockList[position])
    }

    private fun getItemViewType(galleryBlock: GalleryBlock): Int {
        return if (galleryBlock.id == 0) 0 else nowViewType
    }


    // ViewHolders ///////////////////////////////////////
    class SplitterViewHolder constructor(
        private val binding: ItemSplitterBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(gb: GalleryBlock) {
            binding.model = gb
        }
    }

    class GalleryBlockViewHolder1 constructor(
        private val binding: ItemGalleryBlockType1Binding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(gb: GalleryBlock) {
            binding.model = gb
        }
    }

    class GalleryBlockViewHolder2 constructor(
        private val binding: ItemGalleryBlockType2Binding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(gb: GalleryBlock) {
            binding.model = gb
        }
    }


}