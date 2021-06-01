package com.vtsb.hipago.presentation.view.adapter.binding

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.vtsb.hipago.presentation.view.converter.ListConverter
import java.sql.Date

@BindingAdapter("bindThumbnail")
fun bindThumbnail(view: ImageView, url: String) {
    if (url.isEmpty()) return
    Glide.with(view)
        .load("https:$url")
        .into(view)
}

@BindingAdapter("bindTagType1_1", "bindTagType1_2")
fun bindTagType1(view: TextView, format: String, tags: List<String>?) {
    view.text = String.format(format, ListConverter.toString(tags))
}

@BindingAdapter("bindDate")
fun bindDate(view: TextView, date: Date) {

}