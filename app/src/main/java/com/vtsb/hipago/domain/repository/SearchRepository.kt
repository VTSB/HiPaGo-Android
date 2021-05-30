package com.vtsb.hipago.domain.repository

import com.vtsb.hipago.domain.entity.Suggestion

interface SearchRepository {

    fun getSuggestionList(query: String): List<Suggestion>

}