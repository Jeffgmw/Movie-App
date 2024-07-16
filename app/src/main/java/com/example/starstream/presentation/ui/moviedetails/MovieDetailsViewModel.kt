package com.example.starstream.presentation.ui.moviedetails

import androidx.lifecycle.viewModelScope
import com.example.starstream.domain.model.FavoriteMovie
import com.example.starstream.domain.model.MovieDetail
import com.example.starstream.domain.useCase.AddFavorite
import com.example.starstream.domain.useCase.CheckFavorites
import com.example.starstream.domain.useCase.DeleteFavorite
import com.example.starstream.domain.useCase.GetDetails
import com.example.starstream.presentation.ui.base.BaseViewModel
import com.example.starstream.util.MediaType
import com.example.starstream.util.Resource
import com.example.starstream.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieDetailsViewModel (
    private val getDetails: GetDetails,
    private val checkFavorites: CheckFavorites,
    private val deleteFavorite: DeleteFavorite,
    private val addFavorite: AddFavorite
) : BaseViewModel() {

    private val _details = MutableStateFlow(MovieDetail.empty)
    val details get() = _details.asStateFlow()

    private val _isInFavorites = MutableStateFlow(false)
    val isInFavorites get() = _isInFavorites.asStateFlow()

    private lateinit var favoriteMovie: FavoriteMovie

    fun fetchMovieDetails() {
        viewModelScope.launch {
            getDetails(MediaType.MOVIE, id).collect { response ->
                when (response) {
                    is Resource.Success -> {
                        (response.data as MovieDetail).apply {
                            _details.value = this
                            favoriteMovie = FavoriteMovie(
                                id = id,
                                posterPath = posterPath,
                                releaseDate = releaseDate,
                                runtime = runtime,
                                title = title,
                                voteAverage = voteAverage,
                                voteCount = voteCount,
                                date = System.currentTimeMillis()
                            )
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

    private fun checkFavorites() {
        viewModelScope.launch {
            _isInFavorites.value = checkFavorites(MediaType.MOVIE, id)
        }
    }

    fun updateFavorites() {
        viewModelScope.launch {
            if (_isInFavorites.value) {
                deleteFavorite(mediaType = MediaType.MOVIE, movie = favoriteMovie)
                _isInFavorites.value = false
            } else {
                addFavorite(mediaType = MediaType.MOVIE, movie = favoriteMovie)
                _isInFavorites.value = true
            }
        }
    }

    fun initRequests(movieId: Int) {
        id = movieId
        checkFavorites()
        fetchMovieDetails()
    }
}