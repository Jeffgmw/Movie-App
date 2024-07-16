package com.example.starstream.domain.useCase

import com.example.starstream.domain.repository.MovieRepository
import com.example.starstream.util.Constants
import com.example.starstream.util.MediaType
import com.example.starstream.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetSearchResults(
    private val movieRepository: MovieRepository,
) {
    operator fun invoke(mediaType: MediaType, query: String, page: Int): Flow<Resource<Any>> = flow {
        emit(
            when (mediaType) {
                MediaType.MOVIE -> movieRepository.getMovieSearchResults(query, page)
                else -> throw IllegalArgumentException(Constants.ILLEGAL_ARGUMENT_MEDIA_TYPE)
            }
        )
    }
}