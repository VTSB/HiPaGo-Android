package com.vtsb.hipago.presentation.view.converter

class ListConverter {
    companion object {
        fun toString(list: List<String>?): String {
            if (list == null || list.isEmpty()) return "None"
            val builder = StringBuilder()
            for (o in list) builder.append(o).append(',')
            builder.deleteCharAt(builder.length - 1)
            return builder.toString()
        }
    }

}