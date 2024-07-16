package com.example.starstream.domain.useCase

import com.example.starstream.domain.model.FavoriteMovie
import com.example.starstream.domain.repository.MovieRepository
import com.example.starstream.util.Constants
import com.example.starstream.util.MediaType

class AddFavorite(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(
        mediaType: MediaType,
        movie: FavoriteMovie? = null,
    ) {
        when (mediaType) {
            MediaType.MOVIE -> movieRepository.insertMovie(movie!!)
            else -> throw IllegalArgumentException(Constants.ILLEGAL_ARGUMENT_MEDIA_TYPE)
        }
    }
}
