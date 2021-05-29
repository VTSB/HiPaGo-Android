package com.vtsb.hipago.data.datasource.remote.service.original

import com.vtsb.hipago.util.converter.QueryConverter
import com.vtsb.hipago.domain.entity.TagType
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
    fun do_search(query_string: String, language: String): List<Int> {
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
        var results: ArrayList<Int>

        // first result
        results = if (positive_terms.isEmpty()) {
            searchJs.get_galleryids_from_nozomi(null, "index", "all")
        } else {
            val term = positive_terms.removeAt(0)
            searchJs.get_galleryids_for_query(term)
        }

        // positive results
        for (term in positive_terms) {
            val new_results_set: Set<Int> = HashSet(searchJs.get_galleryids_for_query(term, searchLanguage))
            val resultSet = ArrayList<Int>()
            for (no in results) {
                if (new_results_set.contains(no)) resultSet.add(no)
            }
            results = resultSet
        }

        // negative results
        for (term in negative_terms) {
            val new_results_set: Set<Int> = HashSet(searchJs.get_galleryids_for_query(term, searchLanguage))

            // https://stackoverflow.com/questions/1196586/calling-remove-in-foreach-loop-in-java
            val it = results.iterator()
            while (it.hasNext()) {
                val no = it.next()
                if (new_results_set.contains(no)) it.remove()
            }
        }
        return results
    }

}