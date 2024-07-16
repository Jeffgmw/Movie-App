package com.example.starstream.data.mapper

import com.example.starstream.data.local.FavoriteMovieEntity
import com.example.starstream.domain.model.FavoriteMovie

internal fun FavoriteMovie.toFavoriteMovieEntity() = FavoriteMovieEntity(id, posterPath, releaseDate, runtime, title, voteAverage, voteCount, date)

internal fun FavoriteMovieEntity.toFavoriteMovie() = FavoriteMovie(id, posterPath, releaseDate, runtime, title, voteAverage, voteCount, date)


