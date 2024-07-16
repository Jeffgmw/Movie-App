package com.example.starstream.data.remote.api

import com.example.starstream.data.remote.dto.MovieDetailDTO
import com.example.starstream.data.remote.dto.MovieListDTO
import com.example.starstream.data.remote.dto.VideoListDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    @GET("movie/{list_id}")
    suspend fun getMovieList(@Path("list_id") listId: String, @Query("page") page: Int, @Query("region") region: String?): MovieListDTO

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(): MovieListDTO

//    @GET("movie/{movie_id}/videos")
//    suspend fun getTrendingMovieTrailers(@Path("movie_id") movieId: Int): VideoListDTO

    @GET("movie/{movie_id}/videos")
    suspend fun getTrendingMovieTrailers(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String? = "en-US",
    ): VideoListDTO

    @GET("search/movie")
    suspend fun getMovieSearchResults(@Query("query") query: String, @Query("page") page: Int): MovieListDTO

    @GET("movie/{movie_id}?&append_to_response=credits,videos,images")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): MovieDetailDTO

}