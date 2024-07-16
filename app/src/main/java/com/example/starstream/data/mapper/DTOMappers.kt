package com.example.starstream.data.mapper

import com.example.starstream.data.remote.dto.GenreDTO
import com.example.starstream.data.remote.dto.ImageDTO
import com.example.starstream.data.remote.dto.MovieDTO
import com.example.starstream.data.remote.dto.MovieDetailDTO
import com.example.starstream.data.remote.dto.MovieListDTO
import com.example.starstream.data.remote.dto.VideoDTO
import com.example.starstream.data.remote.dto.VideoListDTO
import com.example.starstream.domain.model.Genre
import com.example.starstream.domain.model.Image
import com.example.starstream.domain.model.Movie
import com.example.starstream.domain.model.MovieDetail
import com.example.starstream.domain.model.MovieList
import com.example.starstream.domain.model.Video
import com.example.starstream.domain.model.VideoList


internal fun GenreDTO.toGenre() = Genre(id, name)

internal fun ImageDTO.toImage() = Image(filePath)

internal fun MovieDTO.toMovie() = Movie(character, id, job, overview, posterPath, releaseDate, title, voteAverage)

internal fun MovieDetailDTO.toMovieDetail() = MovieDetail(

    genres.map { it.toGenre() },
    homepage,
    id,
    originalTitle,
    overview,
    posterPath,
    releaseDate,
    runtime,
    status,
    title,
    videos.toVideoList(),
    voteAverage,
    voteCount

)

internal fun MovieListDTO.toMovieList() = MovieList(results.map { it.toMovie() }, totalResults)

internal fun VideoDTO.toVideo() = Video(key, name, publishedAt, site, type)

internal fun VideoListDTO.toVideoList() = VideoList(results.map { it.toVideo() })