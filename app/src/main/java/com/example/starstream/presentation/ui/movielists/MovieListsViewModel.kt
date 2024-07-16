package com.example.starstream.presentation.ui.movielists

import androidx.lifecycle.viewModelScope
import com.example.starstream.data.remote.api.MovieApi
import com.example.starstream.data.remote.dto.VideoListDTO
import com.example.starstream.domain.model.Movie
import com.example.starstream.domain.model.MovieList
import com.example.starstream.domain.model.Video
import com.example.starstream.domain.model.VideoList
import com.example.starstream.domain.useCase.GetList
import com.example.starstream.domain.useCase.GetTrendingVideos
import com.example.starstream.presentation.ui.base.BaseViewModel
import com.example.starstream.util.Constants
import com.example.starstream.util.MediaType
import com.example.starstream.util.Resource
import com.example.starstream.util.UiState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieListsViewModel(
    private val getList: GetList,
    private val getTrendingVideos: GetTrendingVideos,
    private val movieApi: MovieApi
) : BaseViewModel() {

    private val _trendingMovies = MutableStateFlow(emptyList<Movie>())
    val trendingMovies get() = _trendingMovies.asStateFlow()

    private val _popularMovies = MutableStateFlow(emptyList<Movie>())
    val popularMovies get() = _popularMovies.asStateFlow()

    private val _topRatedMovies = MutableStateFlow(emptyList<Movie>())
    val topRatedMovies get() = _topRatedMovies.asStateFlow()

    private val _nowPlayingMovies = MutableStateFlow(emptyList<Movie>())
    val nowPlayingMovies get() = _nowPlayingMovies.asStateFlow()

    private val _upcomingMovies = MutableStateFlow(emptyList<Movie>())
    val upcomingMovies get() = _upcomingMovies.asStateFlow()

    private var pagePopular = 1
    private var pageTopRated = 1
    private var pageNowPlaying = 1
    private var pageUpcoming = 1

    private suspend fun fetchList(listId: String? = null) {
        val page = when (listId) {
            Constants.LIST_ID_POPULAR -> pagePopular
            Constants.LIST_ID_TOP_RATED -> pageTopRated
            Constants.LIST_ID_NOW_PLAYING -> pageNowPlaying
            Constants.LIST_ID_UPCOMING -> pageUpcoming
            else -> null
        }

        getList(
            mediaType = MediaType.MOVIE,
            listId = listId,
            page = page,
        ).collect { response ->
            when (response) {
                is Resource.Success -> {
                    val movieList = (response.data as MovieList).results
                    when (listId) {
                        Constants.LIST_ID_POPULAR -> _popularMovies.value += movieList
                        Constants.LIST_ID_TOP_RATED -> _topRatedMovies.value += movieList
                        Constants.LIST_ID_NOW_PLAYING -> _nowPlayingMovies.value += movieList
                        Constants.LIST_ID_UPCOMING -> _upcomingMovies.value += movieList
                        else -> _trendingMovies.value = movieList
                    }
                    areResponsesSuccessful.add(true)
                    isInitial = false
                }
                is Resource.Error -> {
                    errorText = response.message
                    areResponsesSuccessful.add(false)
                }
            }
        }
    }

    fun onLoadMore(type: Any?) {
        _uiState.value = UiState.loadingState(isInitial)

        when (type as String) {
            Constants.LIST_ID_POPULAR -> pagePopular++
            Constants.LIST_ID_TOP_RATED -> pageTopRated++
            Constants.LIST_ID_NOW_PLAYING -> pageNowPlaying++
            Constants.LIST_ID_UPCOMING -> pageUpcoming++
        }

        viewModelScope.launch {
            coroutineScope { fetchList(type) }
            setUiState()
        }
    }

    suspend fun getTrendingMovieTrailer(movieId: Int): String {
        return try {
            val videoList = movieApi.getTrendingMovieTrailers(movieId).toDomainModel()
            val trailer = videoList.filterVideos(onlyTrailers = true).firstOrNull()
            trailer?.key ?: ""
        } catch (e: Exception) {
            // Handle exceptions
            ""
        }
    }
    // Convert DTO to domain model
    private fun VideoListDTO.toDomainModel(): VideoList {
        return VideoList(results = this.results.map {
            Video(
                key = it.key,
                name = it.name,
                publishedAt = it.publishedAt,
                site = it.site,
                type = it.type
            )
        })
    }

    fun initRequests() {
        viewModelScope.launch {
            coroutineScope {
                launch {
                    fetchList()
                    fetchList(Constants.LIST_ID_POPULAR)
                    fetchList(Constants.LIST_ID_TOP_RATED)
                    fetchList(Constants.LIST_ID_NOW_PLAYING)
                    fetchList(Constants.LIST_ID_UPCOMING)
                }
            }
            setUiState()
        }
    }

    init {
        initRequests()
    }
}
