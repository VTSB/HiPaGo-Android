package com.vtsb.hipago.data.datasource.remote.service.converter.helper

import java.text.SimpleDateFormat
import java.util.*

class DateHelper {
    fun newDateFormat(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.US)
    }
}