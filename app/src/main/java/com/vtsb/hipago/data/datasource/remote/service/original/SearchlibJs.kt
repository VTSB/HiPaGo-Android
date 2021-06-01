package com.vtsb.hipago.data.datasource.remote.service.original

import android.util.Log
import com.vtsb.hipago.data.datasource.remote.service.GalleryDataService
import com.vtsb.hipago.data.datasource.remote.service.converter.ResponseBodyConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.inject.Inject
import kotlin.experimental.and

class SearchlibJs @Inject constructor(
    private val galleryDataService: GalleryDataService,
    private val responseBodyConverter: ResponseBodyConverter
) {

    var regex_sanitize = "[/#]"

    var domain = "ltn.hitomi.la"

    var separator = "-"
    var extension = ".html"
    var galleriesdir = "galleries"
    var index_dir = "tagindex"
    var galleries_index_dir = "galleriesindex"
    var languages_index_dir = "languagesindex"
    var nozomiurl_index_dir = "nozomiurlindex"
    val max_node_size = 464
    val B = 16
    var search_serial = 0
    var search_result_index = -1
    val compressed_nozomi_prefix = "n"

    var tag_index_version: String? = null
    var galleries_index_version: String? = null
    var languages_index_version: String? = null
    var nozomiurl_index_version: String? = null

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                tag_index_version = getIndexVersion("tagindex")
            } catch(t: Throwable) {
                Log.e(this.javaClass.simpleName, "failed to load tagIndex", t)
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                galleries_index_version = getIndexVersion("galleriesindex")
            } catch(t: Throwable) {
                Log.e(this.javaClass.simpleName, "failed to load galleriesIndex", t)
            }
        }
    }

    fun sanitize(input: String): String {
        return input.replace(regex_sanitize.toRegex(), "")
    }

    // or uint8
    fun hash_term(term: String): ShortArray {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hash = messageDigest.digest(term.toByteArray())
        val result = ShortArray(4)
        result[0] = (hash[0].toShort() and 0xff)
        result[1] = (hash[1].toShort() and 0xff)
        result[2] = (hash[2].toShort() and 0xff)
        result[3] = (hash[3].toShort() and 0xff)
        return result
    }

    fun getIndexVersion(name: String): String =
        responseBodyConverter.toIndexVersion(
            galleryDataService.getIndexVersion(
                name, Date().time).execute().body()!!)


}
