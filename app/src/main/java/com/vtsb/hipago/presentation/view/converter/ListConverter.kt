package com.vtsb.hipago.presentation.view.converter

class ListConverter {
    companion object {
        fun toString(array: List<String>?): String {
            if (array == null || array.isEmpty()) return "None"
            val builder = StringBuilder()
            for (o in array) {
                builder.append(o).append(",")
            }
            builder.deleteCharAt(builder.length - 1)
            return builder.toString()
        }
    }

}