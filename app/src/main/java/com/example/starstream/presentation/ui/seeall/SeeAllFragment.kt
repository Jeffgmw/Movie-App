package com.example.starstream.presentation.ui.seeall

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.starstream.R
import com.example.starstream.databinding.FragmentSeeAllBinding
import com.example.starstream.domain.model.Movie
import com.example.starstream.domain.model.Video
import com.example.starstream.presentation.adapter.MovieAdapter
import com.example.starstream.presentation.adapter.VideoAdapter
import com.example.starstream.presentation.ui.base.BaseFragment
import com.example.starstream.util.Constants
import com.example.starstream.util.IntentType
import com.example.starstream.util.MediaType
import com.example.starstream.util.playYouTubeVideo
import org.koin.androidx.viewmodel.ext.android.viewModel

class SeeAllFragment : BaseFragment<FragmentSeeAllBinding>(R.layout.fragment_see_all) {

    private val viewModel: SeeAllViewModel by viewModel()

    var title: String? = null
    var intentType: IntentType? = null

    override val defineBindingVariables: (FragmentSeeAllBinding) -> Unit
        get() = { binding ->
            binding.fragment = this
            binding.lifecycleOwner = this
            binding.viewModel = viewModel
            binding.recyclerView.layoutManager = GridLayoutManager(
                requireContext(),
                if (isLandscape || intentType == IntentType.VIDEOS) 2 else 3
            )
        }

    private var mediaType: MediaType? = null
    private var listId: String? = null
    private var region: String? = null
    private var list: List<Movie>? = null
    private var isLandscape = false

    private val movieAdapter by lazy { MovieAdapter(isGrid = true) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getIntentExtras()
        getList()
    }

    private fun getIntentExtras() {
        arguments?.let { args ->
            intentType = args.getParcelable(Constants.INTENT_TYPE)
            mediaType = args.getParcelable(Constants.MEDIA_TYPE)
            listId = args.getString(Constants.LIST_ID)
            region = args.getString(Constants.REGION)
            list = args.getParcelableArrayList<Parcelable>(Constants.LIST) as List<Movie>?
            isLandscape = args.getBoolean(Constants.IS_LANDSCAPE, false)
            title = args.getString(Constants.TITLE) +
                    if (intentType == IntentType.GENRE) {
                        " " + getString(R.string.title_movies)
                    } else ""
        }
    }

    private fun getList() {
        binding.recyclerView.adapter = when (intentType) {
            IntentType.VIDEOS -> VideoAdapter(true) { videoKey ->
                requireActivity().playYouTubeVideo(videoKey)
            }.apply { submitList(list as List<Video>) }
            else -> {
                when (mediaType) {
                    MediaType.MOVIE -> movieAdapter
                    else -> throw IllegalArgumentException(Constants.ILLEGAL_ARGUMENT_MEDIA_TYPE)
                }.also { collectFlows(listOf(::collectListResult, ::collectUiState)) }
            }
        }

        viewModel.initRequest(intentType, mediaType, id, listId, region)
    }

    private suspend fun collectListResult() {
        viewModel.results.collect { results ->
            when (mediaType) {
                MediaType.MOVIE -> {
                    val movieList = results as Set<Movie>
                    movieAdapter.submitList(movieList.toList())
                }
                else -> throw IllegalArgumentException(Constants.ILLEGAL_ARGUMENT_MEDIA_TYPE)
            }
        }
    }

    private suspend fun collectUiState() {
        viewModel.uiState.collect { state ->
            if (state.isError) showSnackbar(state.errorText!!) {
                viewModel.retryConnection {
                    viewModel.initRequest(intentType, mediaType, id, listId, region)
                }
            }
        }
    }
}
