package com.vtsb.hipago.data.datasource.remote.service.original.helper

import java.nio.ByteBuffer
import kotlin.experimental.and

class DataView(private val buffer: ByteArray) {

    fun getUint8(pos: Int, littleEndian: Boolean): Int {
        // todo : add littleEndian & bigEndian support
        return if (buffer[pos] < 0) -buffer[pos] else buffer[pos].toInt()
    }

    fun getInt32(pos: Int, littleEndian: Boolean): Int {
        // todo : add littleEndian & bigEndian support
        var result = 0
        for (i in 0..4) {
            result = result or (buffer[pos+i].toInt() shl 8 * i)
        }
        return result
    }

    fun getUint64(pos: Int, littleEndian: Boolean): Long {
        // todo : add littleEndian & bigEndian support
        val data = ByteArray(8)
        System.arraycopy(buffer, pos, data, 0, 8)
        return ByteBuffer.wrap(data).long
    }

}