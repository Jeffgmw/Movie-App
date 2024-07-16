package com.example.starstream.presentation.ui.search

import androidx.lifecycle.viewModelScope
import com.example.starstream.domain.model.Movie
import com.example.starstream.domain.model.MovieList
import com.example.starstream.domain.useCase.GetSearchResults
import com.example.starstream.presentation.ui.base.BaseViewModel
import com.example.starstream.util.Constants
import com.example.starstream.util.MediaType
import com.example.starstream.util.Resource
import com.example.starstream.util.UiState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SearchViewModel : BaseViewModel(), KoinComponent {

    private val getSearchResults: GetSearchResults by inject()

    private val _isSearchInitialized = MutableStateFlow(false)
    val isSearchInitialized get() = _isSearchInitialized.asStateFlow()

    private val _query = MutableStateFlow("")
    val query get() = _query.asStateFlow()

    private val _movieResults = MutableStateFlow(emptyList<Movie>())
    val movieResults get() = _movieResults.asStateFlow()

    private val _movieTotalResults = MutableStateFlow(0)
    val movieTotalResults get() = _movieTotalResults.asStateFlow()

    private var pageMovie = 1
    private var isQueryChanged = false

    private suspend fun fetchSearchResults(mediaType: MediaType) {
        val page = when (mediaType) {
            MediaType.MOVIE -> pageMovie

            else -> throw IllegalArgumentException(Constants.ILLEGAL_ARGUMENT_MEDIA_TYPE)
        }

        getSearchResults(mediaType, query.value, page).collect { response ->
            when (response) {
                is Resource.Success -> {
                    when (mediaType) {
                        MediaType.MOVIE -> {
                            val movieList = response.data as MovieList
                            _movieResults.value = if (isQueryChanged) movieList.results else _movieResults.value + movieList.results
                            _movieTotalResults.value = movieList.totalResults
                        }
                        else -> throw IllegalArgumentException(Constants.ILLEGAL_ARGUMENT_MEDIA_TYPE)
                    }

                    areResponsesSuccessful.add(true)
                    isInitial = false
                }
                is Resource.Error -> {
                    errorText = response.message
                    areResponsesSuccessful.add(false)
                }
            }
        }
    }

    fun onLoadMore(type: Any?) {
        _uiState.value = UiState.loadingState(isInitial)
        isQueryChanged = false

        when (type as MediaType) {
            MediaType.MOVIE -> pageMovie++

            else -> throw IllegalArgumentException(Constants.ILLEGAL_ARGUMENT_MEDIA_TYPE)
        }

        viewModelScope.launch {
            coroutineScope { fetchSearchResults(type) }
            setUiState()
        }
    }

    fun fetchInitialSearch(query: String) {
        _uiState.value = UiState.loadingState(isInitial)
        _isSearchInitialized.value = true
        _query.value = query

        isQueryChanged = true
        isInitial = true

        pageMovie = 1

        initRequests()
    }

    fun clearSearchResults() {
        _isSearchInitialized.value = false
        _query.value = ""

        _movieResults.value = emptyList()
    }

    fun initRequests() {
        viewModelScope.launch {
            coroutineScope {
                launch {
                    fetchSearchResults(MediaType.MOVIE)
                }
            }
            setUiState()
        }
    }
}
