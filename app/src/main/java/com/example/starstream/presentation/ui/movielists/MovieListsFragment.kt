package com.example.starstream.presentation.ui.movielists

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.starstream.R
import com.example.starstream.databinding.FragmentMovieListsBinding
import com.example.starstream.presentation.adapter.MovieAdapter
import com.example.starstream.presentation.ui.base.BaseFragment
import com.example.starstream.util.Constants
import com.example.starstream.util.LifecycleRecyclerView
import com.example.starstream.util.LifecycleViewPager
import com.example.starstream.util.playYouTubeVideo
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MovieListsFragment : BaseFragment<FragmentMovieListsBinding>(R.layout.fragment_movie_lists) {

    private val viewModel: MovieListsViewModel by viewModel()

    override val defineBindingVariables: (FragmentMovieListsBinding) -> Unit
        get() = { binding ->
            binding.fragment = this
            binding.lifecycleOwner = viewLifecycleOwner
            binding.viewModel = viewModel
        }

    val adapterTrending = MovieAdapter(isTrending = true, onTrendingFabClick = { playTrailer(it) })
    val adapterPopular = MovieAdapter(onLoadMore = { viewModel.onLoadMore(Constants.LIST_ID_POPULAR) })
    val adapterTopRated = MovieAdapter(onLoadMore = { viewModel.onLoadMore(Constants.LIST_ID_TOP_RATED) })
    val adapterNowPlaying = MovieAdapter(onLoadMore = { viewModel.onLoadMore(Constants.LIST_ID_NOW_PLAYING) })
    val adapterUpcoming = MovieAdapter(onLoadMore = { viewModel.onLoadMore(Constants.LIST_ID_UPCOMING) })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.apply {
            addObserver(LifecycleViewPager(binding.vpTrendings, true))
            addObserver(LifecycleRecyclerView(binding.rvPopular))
            addObserver(LifecycleRecyclerView(binding.rvTopRated))
            addObserver(LifecycleRecyclerView(binding.rvNowPlaying))
            addObserver(LifecycleRecyclerView(binding.rvUpcoming))
        }

        collectFlows(listOf(::collectTrendingMovies, ::collectPopularMovies, ::collectTopRatedMovies, ::collectNowPlayingMovies, ::collectUpcomingMovies, ::collectUiState))
    }

    private fun playTrailer(movieId: Int) {
        lifecycleScope.launch {
            val videoKey = viewModel.getTrendingMovieTrailer(movieId)
            if (videoKey.isEmpty()) {
                showSnackbar(
                    message = getString(R.string.trending_trailer_error),
                    indefinite = false,
                    anchor = true
                )
            } else {
                activity?.playYouTubeVideo(videoKey)
            }
        }
    }

    private suspend fun collectTrendingMovies() {
        viewModel.trendingMovies.collect { trendingMovies ->
            adapterTrending.submitList(trendingMovies.take(10))
        }
    }

    private suspend fun collectPopularMovies() {
        viewModel.popularMovies.collect { popularMovies ->
            adapterPopular.submitList(popularMovies)
        }
    }

    private suspend fun collectTopRatedMovies() {
        viewModel.topRatedMovies.collect { topRatedMovies ->
            adapterTopRated.submitList(topRatedMovies)
        }
    }

    private suspend fun collectNowPlayingMovies() {
        viewModel.nowPlayingMovies.collect { nowPlayingMovies ->
            adapterNowPlaying.submitList(nowPlayingMovies)
        }
    }

    private suspend fun collectUpcomingMovies() {
        viewModel.upcomingMovies.collect { upcomingMovies ->
            adapterUpcoming.submitList(upcomingMovies)
        }
    }

    private suspend fun collectUiState() {
        viewModel.uiState.collect { state ->
            if (state.isError) {
                showSnackbar(
                    message = state.errorText!!,
                    actionText = getString(R.string.button_retry),
                    anchor = true
                ) {
                    viewModel.retryConnection {
                        viewModel.initRequests()
                    }
                }
            }
        }
    }
}
