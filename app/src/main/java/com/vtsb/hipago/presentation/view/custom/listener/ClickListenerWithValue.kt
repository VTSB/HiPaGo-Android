package com.vtsb.hipago.presentation.view.custom.listener

import android.view.View

abstract class ClickListenerWithValue<T>(
    val value: T
): View.OnClickListener