package com.vtsb.hipago.data.datasource.remote.service.original

import android.util.Log
import com.vtsb.hipago.data.datasource.remote.service.GalleryDataService
import com.vtsb.hipago.data.datasource.remote.service.converter.ResponseBodyConverter
import com.vtsb.hipago.data.datasource.remote.service.original.helper.DataView
import com.vtsb.hipago.data.datasource.remote.service.original.pojo.*
import com.vtsb.hipago.util.converter.QueryConverter
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.experimental.and

class SearchJs @Inject constructor(
    private val commonJs: CommonJs,
    private val searchlibJs: SearchlibJs,
    private val galleryDataService: GalleryDataService,
    private val responseBodyConverter: ResponseBodyConverter,
    private val queryConverter: QueryConverter,
) {


    //private val galleryDataService: GalleryDataService? = null
    //private val querySplitter: QuerySplitter? = null

    var regex_get_galleryids_for_query = ":"
    var regex_handle_key_up_in_search_box_1 = "^\\s*$"
    var regex_handle_key_up_in_search_box_2 = "\\s$"

    var pattern_handle_key_up_in_search_box_1 = Pattern.compile(regex_handle_key_up_in_search_box_1)
    var pattern_handle_key_up_in_search_box_2 = Pattern.compile(regex_handle_key_up_in_search_box_2)

//    fun init(galleryDataService: GalleryDataService, querySplitter: QuerySplitter) {
//        SearchJs.galleryDataService = galleryDataService
//        SearchJs.querySplitter = querySplitter
//    }

    fun handle_key_up_in_search_box(query: String): GetSuggestionForQuery {

        // tag_index_version
        //var query = query
        while (searchlibJs.tag_index_version == null) Thread.sleep(1)
        if (query.isEmpty() || pattern_handle_key_up_in_search_box_1.matcher(
                query
            ).matches() || pattern_handle_key_up_in_search_box_2.matcher(query)
                .matches() || query == "-"
        ) {
            ++searchlibJs.search_serial
            return GetSuggestionForQuery(ArrayList<PojoSuggestion>().toArray(arrayOfNulls<PojoSuggestion>(0)), 0)
        }
        val newQuery = query.lowercase(Locale.getDefault())
        val terms: Array<String> = queryConverter.split(newQuery)
        var search_term = terms[terms.size - 1]
        if (newQuery[newQuery.length - 1] == queryConverter.getChar()) {
            search_term = ""
        }
        search_term = search_term.replace("-", "")
        val r: GetSuggestionForQuery = get_suggestions_for_query(search_term, ++searchlibJs.search_serial)
        //Suggestion[] results = r.getArr();
        val results_serial: Int = r.serial
        return if (results_serial != searchlibJs.search_serial) GetSuggestionForQuery(
            ArrayList<PojoSuggestion>().toArray(arrayOfNulls<PojoSuggestion>(0)), 0
        ) else r
    }

    fun get_url_at_range(url: String, range: LongArray): ByteArray {
        // todo : use more optimized network connection request
        val response: Response<ResponseBody> = galleryDataService.get_url_at_range(url, "bytes=" + range[0] + "-" + range[1]).execute()
        val responseBody = response.body()
        val value = arrayOf(ByteArray((range[1] - range[0] + 1).toInt()))
        val offset = intArrayOf(0)

        if (responseBody != null) {
            responseBodyConverter.toByteArray(responseBody,
                object: ResponseBodyConverter.ByteArrayCallback {
                    override fun call(length: Int, byteArray: ByteArray) {
                        val to: Int = offset[0] + length
                        var i = 0
                        while (offset[0] < to) {
                            value[0][offset[0]] = byteArray[i++]
                            offset[0]++
                        }
                    }
                }
            )
        }
        return value[0]
    }

    fun decode_node(data: ByteArray): Node? {
        val view = DataView(data)
        var pos = 0

        val number_of_keys: Int = view.getInt32(pos)
        pos += 4

        val keys = Array(number_of_keys) { ShortArray(4) }

        var top = 0
        for (i in 0 until number_of_keys) {
            val key_size: Int = view.getInt32(pos)
            if (key_size == 0 || key_size > 32) {
                Log.e(this.javaClass.simpleName, "fatal: !key_size || key_size > 32 $key_size")
                return null
            }
            pos += 4

            //pos += key_size;
            val to = pos + key_size
            // keys.push����
            while (pos < to) {
                keys[top][0] = (data[pos].toShort() and 0xff)
                keys[top][1] = (data[pos + 1].toShort() and 0xff)
                keys[top][2] = (data[pos + 2].toShort() and 0xff)
                keys[top][3] = (data[pos + 3].toShort() and 0xff)
                top++
                pos += 4
            }
        }

        // datas
        val number_of_datas: Int = view.getInt32(pos)
        pos += 4
        // searchList.toArray(arrayOfNulls<String>(searchList.size))
        val datas = LinkedList<Data>()
        for (i in 0 until number_of_datas) {
            val offset: Long = view.getUint64(pos)
            pos += 8
            val length: Int = view.getInt32(pos)
            pos += 4
            datas.add(Data(offset, length))
        }

        // subnode_address
        val number_of_subnode_addresses: Int = searchlibJs.B + 1
        val subnode_addresses = LongArray(number_of_subnode_addresses)
        for (i in 0 until number_of_subnode_addresses) {
            val subnode_address: Long = view.getUint64(pos)
            pos += 8
            subnode_addresses[i] = subnode_address
        }
        return Node(
            keys,
            datas.toArray(arrayOfNulls<Data>(datas.size)),
            subnode_addresses)
    }

    fun B_Search(field: String, key: ShortArray, node: Node?, serial: Int): Data? {
        if (node == null || node.keys.isEmpty()) {
            return null
        }
        val lc: LocateKey = locate_key(key, node)
        val there: Int = lc.there
        val where: Int = lc.where
        if (there == 0) {
            return node.datas[where]
        } else if (is_leaf(node)) {
            return null
        }
        if (node.subnode_addresses[where] == 0L) {
            Log.e(SearchJs::class.java.simpleName, "non-root node address 0")
            return null
        }
        val n: Node? = get_node_at_address(field, node.subnode_addresses[where], serial)
        return B_Search(field, key, n, serial)
    }

    private fun compare_arraybuffers(dv1: ShortArray, dv2: ShortArray): Int {
        val top = Math.min(dv1.size, dv2.size)
        for (i in 0 until top) {
            if (dv1[i] < dv2[i]) {
                return -1
            } else if (dv1[i] > dv2[i]) {
                return 1
            }
        }
        return 0
    }

    private fun locate_key(key: ShortArray, node: Node): LocateKey {
        var cmp_result = -1
        var i = 0
        while (i < node.keys.size) {
            cmp_result = compare_arraybuffers(key, node.keys[i])
            if (cmp_result <= 0) {
                break
            }
            i++
        }
        return LocateKey(cmp_result, i)
    }

    private fun is_leaf(node: Node): Boolean {
        for (i in 0 until node.subnode_addresses.size) {
            if (node.subnode_addresses[i] != 0L) {
                return false
            }
        }
        return true
    }

    fun get_node_at_address(field: String, address: Long, serial: Int): Node? {
        if (serial != 0 && serial != searchlibJs.search_serial) {
            return null
        }

        //String url = "//" + SearchlibJs.domain + "/" + SearchlibJs.index_dir + "/" + field + "." + SearchlibJs.tag_index_version + ".index";
        var url = "${searchlibJs.index_dir}/${field}.${searchlibJs.tag_index_version}.index"
        when (field) {
            "galleries" -> {
                //url = "//" + SearchlibJs.domain + "/" + SearchlibJs.galleries_index_dir + "/galleries." + SearchlibJs.galleries_index_version + ".index";
                url = searchlibJs.galleries_index_dir + "/galleries." + searchlibJs.galleries_index_version + ".index"
                while (searchlibJs.galleries_index_version == null) {
                    try {
                        Thread.sleep(1)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
            "languages" ->                 //url = "//" + SearchlibJs.domain + "/" + SearchlibJs.languages_index_dir + "/languages." + SearchlibJs.languages_index_version + ".index";
                url = searchlibJs.languages_index_dir + "/languages." + searchlibJs.languages_index_version + ".index"
            "nozomiurl" ->                 //url = "//" + SearchlibJs.domain + "/" + SearchlibJs.nozomiurl_index_dir + "/nozomiurl." + SearchlibJs.nozomiurl_index_version + ".index";
                url = searchlibJs.nozomiurl_index_dir + "/nozomiurl." + searchlibJs.nozomiurl_index_version + ".index"
        }
        val nodedata = get_url_at_range(url, longArrayOf(address, address + searchlibJs.max_node_size - 1))
        return decode_node(nodedata)
    }

    fun get_suggestions_from_data(field: String, data: Data?): Array<PojoSuggestion> {
        // todo : not checked correctly. check detail later
        if (data == null) {
            return ArrayList<PojoSuggestion>().toArray(arrayOfNulls<PojoSuggestion>(0))
        }

        //String url = SearchlibJs.domain + "/" + SearchlibJs.index_dir + "/" + field + "." + SearchlibJs.tag_index_version + ".data";
        val url: String = searchlibJs.index_dir + "/" + field + "." + searchlibJs.tag_index_version + ".data"
        val offset: Long = data.offset
        val length: Int = data.length
        if (length > 10000 || length <= 0) {
            Log.e(SearchJs::class.java.simpleName, "length $length is too long")
            return ArrayList<PojoSuggestion>().toArray(arrayOfNulls<PojoSuggestion>(0))
        }
        val inbuf = get_url_at_range(url, longArrayOf(offset, offset + length - 1))
        var pos = 0
        val view = DataView(inbuf)
        val number_of_suggestions: Int = view.getInt32(pos)
        val suggestions = LinkedList<PojoSuggestion>()
        pos += 4
        if (number_of_suggestions > 100 || number_of_suggestions <= 0) {
            Log.e(SearchJs::class.java.simpleName, "number_of_suggestions $number_of_suggestions is too long")
            return ArrayList<PojoSuggestion>().toArray(arrayOfNulls<PojoSuggestion>(0))
        }
        for (i in 0 until number_of_suggestions) {
            val ns_sb = StringBuilder()
            var top: Int = view.getInt32(pos)
            pos += 4
            for (c in 0 until top) {
                ns_sb.append(view.getUint8(pos).toInt().toChar())
                pos += 1
            }
            val ns = ns_sb.toString()
            val tag_sb = StringBuilder()
            top = view.getInt32(pos)
            pos += 4
            for (c in 0 until top) {
                tag_sb.append(view.getUint8(pos).toInt().toChar())
                pos += 1
            }
            val tag = tag_sb.toString()
            val count: Int = view.getInt32(pos)
            pos += 4
            val tagName: String = searchlibJs.sanitize(tag)
            var url_suggestion =
                "/" + ns + "/" + tagName + searchlibJs.separator + "all" + searchlibJs.separator + "1" + searchlibJs.extension
            if (ns == "female" || ns == "male") {
                url_suggestion =
                    "/tag/" + ns + ":" + tagName + searchlibJs.separator + "all" + searchlibJs.separator + "1" + searchlibJs.extension
            } else if (ns == "language") {
                url_suggestion =
                    "/index-" + tagName + searchlibJs.separator + "1" + searchlibJs.extension
            }

            suggestions.add(PojoSuggestion(tag, count, url_suggestion, ns))
        }
        return suggestions.toArray(arrayOfNulls<PojoSuggestion>(suggestions.size))
    }

    @Throws(IOException::class)
    fun get_galleryids_from_data(data: Data?): ArrayList<Int> {
        if (data == null) {
            return ArrayList()
        }

        //String url = "//" + SearchlibJs.domain + "/" + SearchlibJs.galleries_index_dir + "/galleries." + SearchlibJs.galleries_index_version + ".data";
        val url: String = searchlibJs.galleries_index_dir + "/galleries." + searchlibJs.galleries_index_version + ".data"
        val offset: Long = data.offset
        val length: Int = data.length
        if (length > 100000000 || length <= 0) {
            Log.e(SearchJs::class.java.simpleName, "length $length is too long")
            return ArrayList()
        }
        val inbuf = get_url_at_range(url, longArrayOf(offset, offset + length - 1))
        var pos = 0
        val view = DataView(inbuf)
        val number_of_gallery_ids: Int = view.getInt32(pos)
        val galleryids = ArrayList<Int>()
        pos += 4
        val expected_length = number_of_gallery_ids * 4 + 4
        if (number_of_gallery_ids > 10000000 || number_of_gallery_ids <= 0) {
            Log.e(
                SearchJs::class.java.simpleName,
                "number_of_gallery_id_s $number_of_gallery_ids is too long"
            )
            return ArrayList()
        } else if (inbuf.size != expected_length) {
            Log.e(
                SearchJs::class.java.simpleName,
                "inBuffer.length " + inbuf.size + " != expected_length " + expected_length
            )
            return ArrayList()
        }
        for (i in 0 until number_of_gallery_ids) {
            galleryids.add(view.getInt32(pos))
            pos += 4
        }
        return galleryids
    }

    @Throws(IOException::class, NoSuchAlgorithmException::class)
    fun get_suggestions_for_query(query: String, serial: Int): GetSuggestionForQuery {
        // todo : add other type splitter
        val newQuery = query.replace("_", " ")
        var field = "global"
        var term = newQuery
        if (newQuery.contains(":")) {
            val sides = query.split(":").toTypedArray()
            field = sides[0]
            term = if (sides.size == 1) "" else sides[1]
//            field = Localization.transformK2ENoThrow(sides[0], 0);
//            if(sides.length == 1)
//                term = "";
//            else
//                term = Localization.transformK2ENoThrow(sides[1], field);
        }
        val key: ShortArray = searchlibJs.hash_term(term)
        val node: Node = get_node_at_address(field, 0, serial) ?: return GetSuggestionForQuery(ArrayList<PojoSuggestion>().toArray(arrayOfNulls<PojoSuggestion>(0)), serial)
        val data: Data = B_Search(field, key, node, serial) ?: return GetSuggestionForQuery(ArrayList<PojoSuggestion>().toArray(arrayOfNulls<PojoSuggestion>(0)), serial)
        return GetSuggestionForQuery(get_suggestions_from_data(field, data), serial)
    }

    @Throws(IOException::class)
    fun get_galleryids_from_nozomi(area: String?, tag: String, language: String): ArrayList<Int> {
        //String nozomi_address = "//" + SearchlibJs.domain + "/" + SearchlibJs.compressed_nozomi_prefix + "/" + tag + "-" + language + CommonJs.nozomiextension;
        var nozomi_address = tag + "-" + language + commonJs.nozomiextension
        if (area != null) {
            //nozomi_address = "//" + SearchlibJs.domain + "/" + SearchlibJs.compressed_nozomi_prefix + "/" + area + "/" + tag + "-" + language + CommonJs.nozomiextension;
            nozomi_address = area + "/" + tag + "-" + language + commonJs.nozomiextension
        }
        val response: Response<ResponseBody> =
            galleryDataService.get_galleryids_from_nozomi(nozomi_address).execute()
        val responseBody = response.body()!!
        // todo : test not safe version function
        return responseBodyConverter.toIntegerArrayListSafe(responseBody)
    }

    @Throws(IOException::class, NoSuchAlgorithmException::class)
    fun get_galleryids_for_query(query: String): ArrayList<Int> {
        return get_galleryids_for_query(query, "all")
    }

    @Throws(IOException::class, NoSuchAlgorithmException::class)
    fun get_galleryids_for_query(query: String, language_: String): ArrayList<Int> {
        if (query.contains(":")) {
            val sides = query.split(regex_get_galleryids_for_query).toTypedArray()
            val ns = sides[0]
            var tag = sides[1]

            //ns = Localization.transformK2ENoThrow(ns, null);
            //tag = Localization.transformK2ENoThrow(tag, ns);
            var area = ns
            var language = language_
            if (ns == "female" || ns == "male") {
                area = "tag"
                tag = "$ns:$tag"
            } else if (ns == "language") {
                area = ""
                language = tag
                tag = "index"
            }
            return get_galleryids_from_nozomi(area, tag, language)
        }
        val key: ShortArray = searchlibJs.hash_term(query)
        val field = "galleries"
        val node: Node = get_node_at_address(field, 0, searchlibJs.search_serial)
            ?: return ArrayList()
        val data: Data = B_Search(field, key, node, searchlibJs.search_serial)
            ?: return ArrayList<Int>()
        return get_galleryids_from_data(data)
    }

}