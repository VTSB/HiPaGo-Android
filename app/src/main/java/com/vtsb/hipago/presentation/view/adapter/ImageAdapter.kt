package com.vtsb.hipago.presentation.view.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("bindThumbnail")
fun bindThumbnail(view: ImageView, url: String) {
    Glide.with(view)
        .load(url)
        .into(view)
}
