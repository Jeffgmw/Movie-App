package com.example.starstream.di

import androidx.room.Room
import com.example.starstream.data.local.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "movie-app-db").build()
    }
    single { get<AppDatabase>().movieDao() }
}
