package com.vtsb.hipago.util.converter

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueryConverter @Inject constructor() {

    private var mode: Int = 0

    fun toQueryFromDataTag(query: String): String {
        return if (getChar() == ' ') query.replace(' ', '_') else query

//        if(getChar() == ' ') {
//            return query.replace(' ', '_');
//        }
//        return query;
    }

    fun justCombine(stringArray: Array<String>): String {
        return justCombine(stringArray, getSimpleString())
    }

    fun justCombine(stringArray: Array<String>, simpleString: String): String {
        val sb = StringBuilder()
        for (string in stringArray) {
            sb.append(string).append(simpleString)
        }
        val len = sb.length
        sb.delete(len - simpleString.length, len)
        return sb.toString()
    }

    fun combine(stringArray: Array<String>): String {
        val sb = StringBuilder()
        val c = getChar()
        if (c == ' ') {
            for (string in stringArray) {
                sb.append(string.replace(c, '_')).append(c)
            }
            val len = sb.length
            sb.delete(len - 1, len)
        } else {
            return justCombine(stringArray, "$c ")
        }
        return sb.toString()
    }

    fun getLastQuery(query: String): String {
        val stringArray = splitAutoConvert(query)
        return if (stringArray.isEmpty()) query else stringArray[stringArray.size - 1]
    }

    fun splitAutoConvert(query: String): Array<String> {
        val arr = split(query)
        if (getChar() == ' ') {
            for (i in arr.indices) {
                arr[i] = arr[i].replace('_', ' ')
            }
            return arr
        }
        for (i in arr.indices) {
            arr[i] = arr[i].trim { it <= ' ' }
        }
        return arr
    }

    fun split(query: String): Array<String> {
        return query.split(getChar().toString()).toTypedArray()
    }

    fun replace(query: String): String {
        val splitter = getChar()
        return if (splitter == ' ') query.replace('_', ' ') else query.trim { it <= ' ' }
//        if (splitter == ' ') {
//            return query.replace('_', ' ');
//        }
//        else {
//            return query.trim();
//        }
    }

    fun transformNotSplitAble(tag: String): String {
        return if (getChar() == ' ') tag.replace(' ', '_') else tag
    }

    // getter / setter
    fun setMode(mode: Int) {
        this.mode = mode
    }

    fun getSimpleStringLength(): Int {
        return if (getChar() == ' ') 1 else 2
    }

    fun getSimpleString(): String {
        val c = getChar()
        return if (c == ' ') c.toString() else "$c "
    }

    fun getChar(): Char {
        return if (mode == 0) {
            ' '
        } else if (mode == 1) {
            ','
        } else {
            Log.e(QueryConverter::class.java.simpleName, "wrong splitMode(getChar): $mode")
            0.toChar()
        }
    }

}