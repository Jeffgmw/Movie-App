package com.example.starstream.domain.repository

import com.example.starstream.domain.model.FavoriteMovie
import com.example.starstream.domain.model.MovieDetail
import com.example.starstream.domain.model.MovieList
import com.example.starstream.domain.model.VideoList
import com.example.starstream.util.Resource

interface MovieRepository {
    suspend fun getMovieList(listId: String, page: Int, region: String?): Resource<MovieList>
    suspend fun getTrendingMovies(): Resource<MovieList>
    suspend fun getTrendingMovieTrailers(movieId: Int): Resource<VideoList>
    suspend fun getMovieSearchResults(query: String, page: Int): Resource<MovieList>
    suspend fun getMovieDetails(movieId: Int): Resource<MovieDetail>
    suspend fun getFavoriteMovies(): List<FavoriteMovie>
    suspend fun movieExists(movieId: Int): Boolean
    suspend fun insertMovie(movie: FavoriteMovie)
    suspend fun deleteMovie(movie: FavoriteMovie)
}