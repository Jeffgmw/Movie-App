package com.example.starstream.util

import com.example.starstream.domain.model.MovieDetail

data class UiState(
    val isLoading: Boolean,
    val isSuccess: Boolean,
    val isError: Boolean,
    val errorText: String? = null,
    val details: MovieDetail? = null
) {
    companion object {
        fun loadingState(isInitial: Boolean = true) = UiState(isLoading = true, isSuccess = !isInitial, isError = false)
        fun successState() = UiState(isLoading = false, isSuccess = true, isError = false)
        fun errorState(isInitial: Boolean = true, errorText: String) = UiState(isLoading = false, isSuccess = !isInitial, isError = true, errorText = errorText)
    }
}