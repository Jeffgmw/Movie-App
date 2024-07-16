package com.example.starstream.presentation.ui.seeall

import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import com.example.starstream.domain.model.MovieList
import com.example.starstream.domain.useCase.GetList
import com.example.starstream.domain.useCase.GetSearchResults
import com.example.starstream.presentation.ui.base.BaseViewModel
import com.example.starstream.util.Constants
import com.example.starstream.util.IntentType
import com.example.starstream.util.MediaType
import com.example.starstream.util.Resource
import com.example.starstream.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SeeAllViewModel(
    private val getList: GetList,
    private val getSearchResults: GetSearchResults
) : BaseViewModel() {

    private val _results = MutableStateFlow(setOf<Any>())
    val results get() = _results.asStateFlow()

    private var intentType: Parcelable? = null
    private var mediaType: Parcelable? = null
    private var detailId: Int? = null
    private var listId: String? = null

    private var region: String? = null
    private var page = 1

    private fun fetchList() {
        viewModelScope.launch {
            getList(
                mediaType = mediaType as MediaType,
                listId = listId,
                page = page,
                region = region
            ).collect { response ->
                when (response) {
                    is Resource.Success -> {
                        _results.value += when (mediaType) {
                            MediaType.MOVIE -> (response.data as MovieList).results
                            else -> throw IllegalArgumentException(Constants.ILLEGAL_ARGUMENT_MEDIA_TYPE)
                        }
                        _uiState.value = UiState.successState()
                    }
                    is Resource.Error -> {
                        _uiState.value = UiState.errorState(errorText = response.message)
                    }
                }
            }
        }
    }

    private fun fetchSearchResults() {
        viewModelScope.launch {
            getSearchResults(
                mediaType = mediaType as MediaType,
                query = listId!!,
                page = page
            ).collect { response ->
                when (response) {
                    is Resource.Success -> {
                        _results.value += when (mediaType) {
                            MediaType.MOVIE -> (response.data as MovieList).results
                            else -> throw IllegalArgumentException(Constants.ILLEGAL_ARGUMENT_MEDIA_TYPE)
                        }
                        _uiState.value = UiState.successState()
                    }
                    is Resource.Error -> {
                        _uiState.value = UiState.errorState(errorText = response.message)
                    }
                }
            }
        }
    }

    fun onLoadMore(type: Any?) {
        _uiState.value = UiState.loadingState()
        page++

        when (intentType) {
            IntentType.LIST -> fetchList()
            IntentType.SEARCH -> fetchSearchResults()
        }
    }

    fun initRequest(intentType: Parcelable?, mediaType: Parcelable?, detailId: Int?, listId: String?, region: String?) {
        this.intentType = intentType
        this.mediaType = mediaType
        this.detailId = detailId
        this.listId = listId
        this.region = region

        when (intentType) {
            IntentType.LIST -> fetchList()
            IntentType.SEARCH -> fetchSearchResults()
            else -> _uiState.value = UiState.successState()
        }
    }

}
