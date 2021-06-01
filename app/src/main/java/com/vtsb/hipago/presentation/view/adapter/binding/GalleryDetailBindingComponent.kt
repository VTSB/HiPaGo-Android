package com.vtsb.hipago.presentation.view.adapter.binding

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingComponent
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.vtsb.hipago.R
import com.vtsb.hipago.databinding.ItemGalleryBlockType1Binding
import com.vtsb.hipago.databinding.ItemTagBinding
import com.vtsb.hipago.databinding.ItemTagListBinding
import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.domain.entity.GalleryBlockType
import com.vtsb.hipago.domain.entity.TagType
import com.vtsb.hipago.presentation.view.custom.listener.ClickListenerWithValue
import com.vtsb.hipago.util.converter.QueryConverter
import com.vtsb.hipago.util.converter.TagConverter
import javax.inject.Inject

// https://stackoverflow.com/questions/41926128/why-bindingadapter-must-be-static-method
class GalleryDetailBindingComponent @Inject constructor(
    private val tagConverter: TagConverter,
    private val queryConverter: QueryConverter,
): DataBindingComponent {



    @BindingAdapter("inflateTags")
    fun inflateTags(layout: LinearLayout, galleryBlock: GalleryBlock) {
        val inflater = LayoutInflater.from(layout.context)

        layout.addView(getTagListView(inflater, layout, R.string.number, listOf(galleryBlock.id.toString()), TagType.ID))
        when (galleryBlock.type) {
            GalleryBlockType.MI_NOT_DETAILED-> {
                layout.addView(getTagListView(inflater, layout, R.string.type, galleryBlock.tags[TagType.TYPE], TagType.TYPE))
                layout.addView(getTagListView(inflater, layout, R.string.language, galleryBlock.tags[TagType.LANGUAGE], TagType.LANGUAGE))
                layout.addView(getTagListView(inflater, layout, R.string.artist, galleryBlock.tags[TagType.ARTIST], TagType.ARTIST))
                layout.addView(getTagListView(inflater, layout, R.string.series, galleryBlock.tags[TagType.SERIES], TagType.SERIES))
                layout.addView(getTagListView(inflater, layout, R.string.tag, galleryBlock.tags[TagType.TAG], TagType.TAG))

            }
            GalleryBlockType.MI_DETAILED-> {
                layout.addView(getTagListView(inflater, layout, R.string.type, galleryBlock.tags[TagType.TYPE], TagType.TYPE))
                layout.addView(getTagListView(inflater, layout, R.string.language, galleryBlock.tags[TagType.LANGUAGE], TagType.LANGUAGE))
                layout.addView(getTagListView(inflater, layout, R.string.group, galleryBlock.tags[TagType.GROUP], TagType.GROUP))
                layout.addView(getTagListView(inflater, layout, R.string.artist, galleryBlock.tags[TagType.ARTIST], TagType.ARTIST))
                layout.addView(getTagListView(inflater, layout, R.string.series, galleryBlock.tags[TagType.SERIES], TagType.SERIES))
                layout.addView(getTagListView(inflater, layout, R.string.character, galleryBlock.tags[TagType.CHARACTER], TagType.CHARACTER))
                layout.addView(getTagListView(inflater, layout, R.string.tag, galleryBlock.tags[TagType.TAG], TagType.TAG))
            }
            else -> {}
        }
    }


    @BindingAdapter("inflateData", "inflateListener")
    fun inflateData(layout: ChipGroup, tags: List<String>?, listener: ClickListenerWithValue<TagType>?) {
        val inflater = LayoutInflater.from(layout.context)
        layout.removeAllViews()
        if (tags == null) {
            val binding: ItemTagBinding = ItemTagBinding.inflate(inflater, layout, false)
            binding.tag = "None"
            layout.addView(binding.root)
        } else {
            for (tag in tags) {
                val binding: ItemTagBinding = ItemTagBinding.inflate(inflater, layout, false)
                binding.tag = tag
                binding.itTag.setOnClickListener(listener)
                val c = tag[tag.length - 1]
                if (c == '♂') {
                    binding.itTag.setChipBackgroundColorResource(R.color.chip_tag_male)
                } else if (c == '♀') {
                    binding.itTag.setChipBackgroundColorResource(R.color.chip_tag_female)
                }
                layout.addView(binding.root)
            }
        }
    }

    private fun getTagListView(inflater: LayoutInflater, layout: LinearLayout, resourceId: Int, tags: List<String>?, tagType: TagType): View {
        val binding = ItemTagListBinding.inflate(inflater, layout, false)
        binding.info = layout.resources.getString(resourceId)
        binding.tags = tags
        binding.listener = object : ClickListenerWithValue<TagType>(tagType) {
            override fun onClick(view: View) {
                val chip = view as Chip
                val text = chip.text as String
                val type: TagType = value
                if (type === TagType.ID) {
                    val clipboardManager = view.getContext()
                        .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("GalleryNo", text)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(view.getContext(), "ID가 복사되었습니다.($text)", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val (tagType1, tag) = tagConverter.toOriginalDataTag(type, text)
                    moveToGalleryListFragment(view, "${tagType1.otherName}:$tag")
                }
            }

            private fun moveToGalleryListFragment(view: View, query: String) {
                val dataQuery = queryConverter.toQueryFromDataTag(query)
//                Navigation.findNavController(view).navigate(
//                    GalleryDetailFragmentDirections
//                        .actionGalleryDetail()
//                        .setQuery(dataQuery)
//                )
            }
        }
        return binding.root
    }


}