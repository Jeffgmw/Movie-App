package com.example.starstream.domain.useCase

import com.example.starstream.domain.model.VideoList
import com.example.starstream.domain.repository.MovieRepository
import com.example.starstream.util.Constants
import com.example.starstream.util.MediaType
import com.example.starstream.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTrendingVideos(
    private val movieRepository: MovieRepository,
) {
    operator fun invoke(mediaType: MediaType, id: Int): Flow<Resource<VideoList>> = flow {
        emit(
            when (mediaType) {
                MediaType.MOVIE -> movieRepository.getTrendingMovieTrailers(id)
                else -> throw IllegalArgumentException(Constants.ILLEGAL_ARGUMENT_MEDIA_TYPE)
            }
        )
    }
}