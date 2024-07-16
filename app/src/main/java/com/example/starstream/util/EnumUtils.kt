package com.example.starstream.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class ImageQuality(val imageBaseUrl: String) {
    LOW("https://image.tmdb.org/t/p/w300"),
    MEDIUM("https://image.tmdb.org/t/p/w500"),
    HIGH("https://image.tmdb.org/t/p/w780"),
    ORIGINAL("https://image.tmdb.org/t/p/original")
}

@Parcelize
enum class IntentType : Parcelable {
    LIST, VIDEOS, CAST, SEARCH, GENRE, DETAILS
}

@Parcelize
enum class MediaType : Parcelable {
    MOVIE, PERSON
}