package com.vtsb.hipago.data.datasource.remote.service.converter

import android.util.Log
import okhttp3.Response
import java.io.IOException

class ResponseConverter(private val responseBodyConverter: ResponseBodyConverter) {

    fun toContentLength(response: Response): Long {
        var contentLength = response.header("Content-Range")
        if (contentLength != null) {
            contentLength = contentLength.substring(contentLength.lastIndexOf("/") + 1)
            return contentLength.toLong()
        }
        Log.e(ResponseConverter::class.java.simpleName, "failed to get length:$response")
        return 0
    }

    @Throws(IOException::class)
    fun toByteArray(response: Response, callback: ResponseBodyConverter.ByteArrayCallback) {
        val responseBody = response.body
        if (responseBody == null) {
            response.close()
            throw NullPointerException()
        }
        responseBodyConverter.toByteArray(responseBody, callback)
    }

}