package com.vtsb.hipago.data.datasource.remote.service.converter

import com.vtsb.hipago.data.datasource.remote.service.original.helper.DataView
import com.vtsb.hipago.util.Constants.BUFFER_SIZE
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.IOException
import java.io.InputStream
import java.util.*

class ResponseBodyConverter {

    @Throws(IOException::class)
    fun toElements(responseBody: ResponseBody): Elements {
        val string: String
        responseBody.use { rb ->
            string = rb.string()
        }
        return Jsoup.parse(string).allElements
    }

    @Throws(IOException::class)
    fun toIndexVersion(responseBody: ResponseBody): String {
        val builder = StringBuilder()
        toByteArray(
            responseBody,
            object: ByteArrayCallback {
                override fun call(length: Int, byteArray: ByteArray) {
                    builder.append(String(byteArray, 0, length))
                }
            })
        return builder.toString()
    }

    @Throws(IOException::class)
    fun toIntegerArrayList(responseBody: ResponseBody): ArrayList<Int> {
        val integerArrayList = LinkedList<Int>()
        toByteArray(responseBody,
            object: ByteArrayCallback {
                override fun call(length: Int, byteArray: ByteArray) {
                    val view = DataView(byteArray)
                    var i = 0
                    while (i < length) {
                        val num: Int = view.getInt32(i, false)
                        integerArrayList.add(num)
                        i += 4
                    }
                }
            })
        return ArrayList(integerArrayList)
    }

    @Throws(IOException::class)
    fun toIntegerArrayListSafe(responseBody: ResponseBody): ArrayList<Int> {
        val integerArrayList = ArrayList<Int>()
        toByteArray(
            responseBody,
            object: ByteArrayCallback {
                private var top = 0
                private val buffer = ByteArray(4)
                override fun call(length: Int, byteArray: ByteArray) {
                    for (i in 0..length) {
                        buffer[top] = byteArray[i]
                        if (++top == 4) {
                            val view = DataView(buffer)
                            integerArrayList.add(view.getInt32(0, false))
                            top = 0
                        }
                    }
                }
            })
        return integerArrayList
    }

    fun toByteArray(responseBody: ResponseBody, callback: ByteArrayCallback) {
        val inputStream: InputStream = responseBody.byteStream()
        val byteArray = ByteArray(BUFFER_SIZE)
        var length: Int
        try {
            while (inputStream.read(byteArray).also { length = it } != -1) {
                callback.call(length, byteArray)
            }
        } finally {
            inputStream.close()
            responseBody.close()
        }
    }

    interface ByteArrayCallback {
        fun call(length: Int, byteArray: ByteArray)
    }


}