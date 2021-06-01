package com.vtsb.hipago.presentation.view.adapter.binding

import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.chip.ChipGroup
import com.vtsb.hipago.R
import com.vtsb.hipago.databinding.ItemTagBinding
import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.domain.entity.TagType
import com.vtsb.hipago.presentation.view.converter.ListConverter
import com.vtsb.hipago.presentation.view.custom.listener.ClickListenerWithValue
import java.sql.Date

@BindingAdapter("bindThumbnail")
fun bindThumbnail(view: ImageView, url: String) {
    if (url.isEmpty()) return
    Glide.with(view)
        .load("https:$url")
        .into(view)
}

@BindingAdapter("bindBigThumbnail")
fun bindBigThumbnail(view: ImageView, url: String) {
    Glide.with(view.context)
        .load("https:$url")
        .placeholder(R.drawable.ic_baseline_hourglass_full_24).into(view)
}

@BindingAdapter("bindTagType1_1", "bindTagType1_2")
fun bindTagType1(view: TextView, format: String, tags: List<String>?) {
    view.text = String.format(format, ListConverter.toString(tags))
}
