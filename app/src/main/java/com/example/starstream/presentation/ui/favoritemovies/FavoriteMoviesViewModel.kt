package com.example.starstream.presentation.ui.favoritemovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starstream.domain.model.FavoriteMovie
import com.example.starstream.domain.useCase.AddFavorite
import com.example.starstream.domain.useCase.DeleteFavorite
import com.example.starstream.domain.useCase.GetFavorites
import com.example.starstream.util.MediaType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteMoviesViewModel(
    private val getFavorites: GetFavorites,
    private val deleteFavorite: DeleteFavorite,
    private val addFavorite: AddFavorite
) : ViewModel() {

    private val _favoriteMovies = MutableStateFlow(emptyList<FavoriteMovie>())
    val favoriteMovies get() = _favoriteMovies.asStateFlow()

    fun fetchFavoriteMovies() {
        viewModelScope.launch {
            getFavorites(MediaType.MOVIE).collect {
                _favoriteMovies.value = it as List<FavoriteMovie>
            }
        }
    }

    fun removeMovieFromFavorites(movie: FavoriteMovie) {
        viewModelScope.launch {
            deleteFavorite(mediaType = MediaType.MOVIE, movie = movie)
            fetchFavoriteMovies()
        }
    }

    fun addMovieToFavorites(movie: FavoriteMovie) {
        viewModelScope.launch {
            addFavorite(mediaType = MediaType.MOVIE, movie = movie)
            fetchFavoriteMovies()
        }
    }

    init {
        fetchFavoriteMovies()
    }
}
