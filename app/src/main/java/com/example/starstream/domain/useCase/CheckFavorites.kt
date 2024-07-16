package com.example.starstream.domain.useCase

import com.example.starstream.domain.repository.MovieRepository
import com.example.starstream.util.Constants
import com.example.starstream.util.MediaType

class CheckFavorites(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(
        mediaType: MediaType,
        id: Int
    ): Boolean = when (mediaType) {
        MediaType.MOVIE -> movieRepository.movieExists(id)
        else -> throw IllegalArgumentException(Constants.ILLEGAL_ARGUMENT_MEDIA_TYPE)
    }
}
