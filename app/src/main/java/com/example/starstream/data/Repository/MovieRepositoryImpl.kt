package com.example.starstream.data.Repository

import com.example.starstream.data.local.MovieDao
import com.example.starstream.data.mapper.toFavoriteMovie
import com.example.starstream.data.mapper.toFavoriteMovieEntity
import com.example.starstream.data.mapper.toMovieDetail
import com.example.starstream.data.mapper.toMovieList
import com.example.starstream.data.mapper.toVideoList
import com.example.starstream.data.remote.api.MovieApi
import com.example.starstream.domain.model.FavoriteMovie
import com.example.starstream.domain.model.MovieDetail
import com.example.starstream.domain.model.MovieList
import com.example.starstream.domain.model.VideoList
import com.example.starstream.domain.repository.MovieRepository
import com.example.starstream.util.Resource
import com.example.starstream.util.SafeApiCall

class MovieRepositoryImpl(
    private val api: MovieApi,
    private val safeApiCall: SafeApiCall,
    private val dao: MovieDao
) : MovieRepository {
    override suspend fun getMovieList(listId: String, page: Int, region: String?): Resource<MovieList> = safeApiCall.execute {
        api.getMovieList(listId, page, region).toMovieList()
    }

    override suspend fun getTrendingMovies(): Resource<MovieList> = safeApiCall.execute {
        api.getTrendingMovies().toMovieList()
    }

    override suspend fun getTrendingMovieTrailers(movieId: Int): Resource<VideoList> = safeApiCall.execute {
        api.getTrendingMovieTrailers(movieId).toVideoList()
    }

    override suspend fun getMovieSearchResults(query: String, page: Int): Resource<MovieList> = safeApiCall.execute {
        api.getMovieSearchResults(query, page).toMovieList()
    }

    override suspend fun getMovieDetails(movieId: Int): Resource<MovieDetail> = safeApiCall.execute {
        api.getMovieDetails(movieId).toMovieDetail()
    }

    override suspend fun getFavoriteMovies(): List<FavoriteMovie> = dao.getAllMovies().map { it.toFavoriteMovie() }

    override suspend fun movieExists(movieId: Int): Boolean = dao.movieExists(movieId)


    override suspend fun insertMovie(movie: FavoriteMovie) {
        dao.insertMovie(movie.toFavoriteMovieEntity())
    }

    override suspend fun deleteMovie(movie: FavoriteMovie) {
        dao.deleteMovie(movie.toFavoriteMovieEntity())
    }
}
