package com.example.starstream.presentation.ui.favoritemovies

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.starstream.R
import com.example.starstream.databinding.FragmentFavoriteMoviesBinding
import com.example.starstream.domain.model.FavoriteMovie
import com.example.starstream.presentation.adapter.FavoriteMovieAdapter
import com.example.starstream.presentation.ui.base.BaseFragment
import com.example.starstream.util.LifecycleRecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteMoviesFragment : BaseFragment<FragmentFavoriteMoviesBinding>(R.layout.fragment_favorite_movies) {

    private val viewModel: FavoriteMoviesViewModel by viewModel()

    override val defineBindingVariables: (FragmentFavoriteMoviesBinding) -> Unit
        get() = { binding ->
            binding.fragment = this
            binding.lifecycleOwner = viewLifecycleOwner
            binding.viewModel = viewModel
        }

//    val adapterFavorites = FavoriteMovieAdapter { removeMovie(it) }

     val adapterFavorites by lazy {
        FavoriteMovieAdapter(
            onRemoveClicked = { movie -> removeMovie(movie) },
            onItemClicked = { movie -> navigateToMovieDetails(movie) }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.addObserver(LifecycleRecyclerView(binding.recyclerView))
        collectFlows(listOf(::collectFavoriteMovies))
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchFavoriteMovies()
    }

    private fun removeMovie(movie: FavoriteMovie) {
        viewModel.removeMovieFromFavorites(movie)
        showSnackbar(
            message = getString(R.string.snackbar_removed_item),
            actionText = getString(R.string.snackbar_action_undo),
            anchor = true
        ) {
            viewModel.addMovieToFavorites(movie)
        }
    }

    private fun navigateToMovieDetails(movie: FavoriteMovie) {
        val navController = findNavController()
        val action = FavoriteMoviesFragmentDirections.actionFavoriteMoviesFragmentToMovieDetailsFragment(id, backgroundColor)
        navController.navigate(action)
    }

    private suspend fun collectFavoriteMovies() {
        viewModel.favoriteMovies.collect { favoriteMovies ->
            adapterFavorites.submitList(favoriteMovies)
        }
    }
}