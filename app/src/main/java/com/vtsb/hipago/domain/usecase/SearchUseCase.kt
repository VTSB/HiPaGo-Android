package com.vtsb.hipago.domain.usecase

import com.vtsb.hipago.domain.entity.Suggestion
import com.vtsb.hipago.domain.repository.SearchRepository
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {

    fun getSuggestions(query: String): List<Suggestion> =
        searchRepository.getSuggestionList(query)

}