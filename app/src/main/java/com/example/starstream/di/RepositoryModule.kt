package com.example.starstream.di

import com.example.starstream.data.Repository.MovieRepositoryImpl
import com.example.starstream.domain.repository.MovieRepository
import com.example.starstream.util.SafeApiCall
import org.koin.dsl.module

val repositoryModule = module {
    single<MovieRepository> { MovieRepositoryImpl(get(), get(), get()) }

    single { SafeApiCall(get()) }
}
