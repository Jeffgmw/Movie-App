package com.example.starstream.domain.useCase

import com.example.starstream.domain.repository.MovieRepository
import com.example.starstream.util.Constants
import com.example.starstream.util.MediaType
import com.example.starstream.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetList (
    private val movieRepository: MovieRepository,
) {
    operator fun invoke(mediaType: MediaType, listId: String?, page: Int? = null, region: String? = null): Flow<Resource<Any>> = flow {
        emit(
            when (mediaType) {
                MediaType.MOVIE -> if (listId == null) movieRepository.getTrendingMovies() else movieRepository.getMovieList(listId, page!!, region)
                else -> throw IllegalArgumentException(Constants.ILLEGAL_ARGUMENT_MEDIA_TYPE)
            }
        )
    }
}