package com.vtsb.hipago.data.datasource.remote.service.original.helper

import java.nio.ByteBuffer

class DataView(private val buffer: ByteArray) {

    fun getUint8(pos: Int): UByte =
        buffer[pos].toUByte()

    fun getInt32(pos: Int): Int {
        var result = 0
        for (i in 0 until 4) {
            result = result or (getUint8(pos + i).toInt() shl 8 * (3-i))
        }
        return result
    }

    fun getUint64(pos: Int): Long {
        val data = ByteArray(8)
        System.arraycopy(buffer, pos, data, 0, 8)
        return ByteBuffer.wrap(data).long
    }

}