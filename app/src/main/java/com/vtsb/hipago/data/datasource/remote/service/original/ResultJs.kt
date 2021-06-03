package com.vtsb.hipago.data.datasource.remote.service.original

import android.util.Log
import com.vtsb.hipago.domain.entity.TagType
import com.vtsb.hipago.util.converter.QueryConverter
import kotlinx.coroutines.*
import java.io.IOException
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

class ResultJs @Inject constructor(
    private val searchJs: SearchJs,
    private val queryConverter: QueryConverter
) {

    var regex_do_search = "^-"

    var pattern_do_search = Pattern.compile(regex_do_search)


    // edited version of do_search function
    @Throws(IOException::class, NoSuchAlgorithmException::class)
    suspend fun do_search(query_string: String, language: String): List<Int> {
        val positive_terms: MutableList<String> = ArrayList()
        val negative_terms: MutableList<String> = ArrayList()
        val terms: Array<String> =
            queryConverter.splitAutoConvert(query_string.lowercase(Locale.getDefault()).trim { it <= ' ' })
        var searchLanguage: String? = null
        for (s in terms) {
            if (pattern_do_search.matcher(s).matches()) {
                negative_terms.add(s)
            } else {
                positive_terms.add(s)
                val sides = s.split(":").toTypedArray()
                if (sides.size != 2) continue
                if (TagType.LANGUAGE.otherName == sides[0]) {
                    searchLanguage = sides[1]
                }
            }
        }
        if (searchLanguage == null) {
            searchLanguage = language
        }


        val firstResultDeferred: Deferred<List<Int>>
        val positiveTermDeferredList: MutableList<Deferred<List<Int>>> = ArrayList(positive_terms.size)
        val negativeTermDeferredList: MutableList<Deferred<List<Int>>> = ArrayList(negative_terms.size)

        coroutineScope {
            firstResultDeferred = if (positive_terms.isEmpty()) {
                async { searchJs.get_galleryids_from_nozomi(null, "index", "all") }
            } else {
                val term = positive_terms.removeAt(0)
                async { searchJs.get_galleryids_for_query(term) }
            }

            for (term in positive_terms) {
                positiveTermDeferredList.add( async { searchJs.get_galleryids_for_query(term, searchLanguage) } )
            }
            for (term in negative_terms) {
                negativeTermDeferredList.add( async { searchJs.get_galleryids_for_query(term, searchLanguage) } )
            }
        }

        var results: List<Int> = firstResultDeferred.await()
        for (deferred in positiveTermDeferredList) {
            val newResultSet: Set<Int> = HashSet(deferred.await())
            results = results.filter { newResultSet.contains(it) }
        }
        for (deferred in negativeTermDeferredList) {
            val newResultSet: Set<Int> = HashSet(deferred.await())
            results = results.filterNot { newResultSet.contains(it) }
        }
        return results
    }

}