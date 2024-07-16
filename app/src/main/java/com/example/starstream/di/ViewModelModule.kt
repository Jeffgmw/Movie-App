package com.example.starstream.di

import com.example.starstream.domain.useCase.AddFavorite
import com.example.starstream.domain.useCase.CheckFavorites
import com.example.starstream.domain.useCase.DeleteFavorite
import com.example.starstream.domain.useCase.GetDetails
import com.example.starstream.domain.useCase.GetFavorites
import com.example.starstream.domain.useCase.GetList
import com.example.starstream.domain.useCase.GetSearchResults
import com.example.starstream.domain.useCase.GetTrendingVideos
import com.example.starstream.presentation.ui.favoritemovies.FavoriteMoviesViewModel
import com.example.starstream.presentation.ui.moviedetails.MovieDetailsViewModel
import com.example.starstream.presentation.ui.movielists.MovieListsViewModel
import com.example.starstream.presentation.ui.search.SearchViewModel
import com.example.starstream.presentation.ui.seeall.SeeAllViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { SeeAllViewModel(get(), get()) }
    viewModel { SearchViewModel() }
    viewModel { MovieListsViewModel(get(),get(), get()) }

    viewModel {
        MovieDetailsViewModel(
            getDetails = get(),
            checkFavorites = get(),
            deleteFavorite = get(),
            addFavorite = get()
        )
    }

    viewModel { FavoriteMoviesViewModel(get(), get(), get()) }

}

val useCaseModule = module {
    single { GetList(get()) }
    single { GetTrendingVideos(get()) }
    single { AddFavorite(get()) }
    single { CheckFavorites(get()) }
    single { DeleteFavorite(get()) }
    single { GetDetails(get()) }
    single { GetSearchResults(get()) }
    single { GetFavorites(get()) }

}
