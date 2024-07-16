package com.example.starstream.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import com.example.starstream.BuildConfig
import com.example.starstream.data.remote.api.MovieApi
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.themoviedb.org/3/"
private const val QUERY_LANGUAGE = "en"
private const val IMAGE_LANGUAGE = "en,null"
private const val CACHE_SIZE = 1024 * 1024 * 10L // 10 MB
private const val CACHE_MAX_AGE = 60 * 60  // 1 hour
private const val CACHE_MAX_STALE = 60 * 60 * 24 * 7 // 7 days

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
    } else {
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        activeNetwork != null && activeNetwork.isConnected
    }
}

val networkModule = module {

    // Cache Interceptor
    single(named("cacheInterceptor")) {
        Interceptor { chain ->
            val headerName = "Cache-Control"
            val headerValue = if (isNetworkAvailable(get())) "public, max-age=$CACHE_MAX_AGE" else "public, only-if-cached, max-stale=$CACHE_MAX_STALE"
            val request = chain.request()
                .newBuilder()
                .header(headerName, headerValue)
                .build()
            chain.proceed(request)
        }
    }

    // Request Interceptor
    single(named("requestInterceptor")) {
        Interceptor { chain ->
            val originalRequest = chain.request()
            val originalUrl = originalRequest.url()
            val url = originalUrl.newBuilder()
                .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .addQueryParameter("language", QUERY_LANGUAGE)
                .addQueryParameter("include_image_language", IMAGE_LANGUAGE)
                .build()
            val requestBuilder = originalRequest.newBuilder().url(url)
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    // OkHttpClient
    single {
        OkHttpClient.Builder()
            .cache(Cache(get<Context>().cacheDir, CACHE_SIZE))
            .addInterceptor(get(named("cacheInterceptor")))
            .addInterceptor(get(named("requestInterceptor")))
            .build()
    }

    // Retrofit
    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // MovieApi Service
    single { get<Retrofit>().create(MovieApi::class.java) }

}
