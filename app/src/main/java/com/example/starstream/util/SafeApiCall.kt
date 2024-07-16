package com.example.starstream.util

import android.content.Context
import com.example.starstream.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class SafeApiCall(val context: Context) {
    suspend inline fun <T> execute(crossinline body: suspend () -> T): Resource<T> {
        return try {
            Resource.Success(
                withContext(Dispatchers.IO) { body() }
            )
        } catch (e: Exception) {
            Resource.Error(
                when (e) {
                    is IOException -> context.getString(R.string.error_connection)
                    is HttpException -> {
                        if (e.code() == 504) context.getString(R.string.error_connection)
                        else context.getString(R.string.error_service)
                    }
                    else -> context.getString(R.string.error_unknown)
                }
            )
        }
    }
}
