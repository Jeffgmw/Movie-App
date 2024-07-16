package com.example.starstream.domain.useCase

import com.example.starstream.domain.repository.MovieRepository
import com.example.starstream.util.Constants
import com.example.starstream.util.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetFavorites(
    private val movieRepository: MovieRepository,
) {
    operator fun invoke(mediaType: MediaType): Flow<List<Any>> = flow {
        emit(
            when (mediaType) {
                MediaType.MOVIE -> movieRepository.getFavoriteMovies()
                else -> throw IllegalArgumentException(Constants.ILLEGAL_ARGUMENT_MEDIA_TYPE)
            }
        )
    }
}