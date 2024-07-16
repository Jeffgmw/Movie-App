package com.example.starstream

import android.app.Application
import com.example.starstream.di.dataStoreModule
import com.example.starstream.di.databaseModule
import com.example.starstream.di.networkModule
import com.example.starstream.di.repositoryModule
import com.example.starstream.di.useCaseModule
import com.example.starstream.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(listOf(
                databaseModule, dataStoreModule, networkModule,
                repositoryModule, viewModelModule, useCaseModule
            ))
        }
    }
}
