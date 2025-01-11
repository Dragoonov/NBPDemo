package com.moonfly.nbpdemo.data

import com.moonfly.nbpdemo.domain.repository.Response
import java.io.IOException

object ApiHelper {

    suspend fun <R, T> executeCallWithErrorHandling(
        call: suspend () -> retrofit2.Response<R>,
        successMapper: suspend (R) -> Response<T>
    ): Response<T> {
        return try {
            val response = call()
            val body = response.body()
            when {
                response.isSuccessful && body != null -> {
                    successMapper(body)
                }

                !response.isSuccessful && response.code() in 400..499 -> {
                    Response.Error.HttpError(response.code(), Throwable(response.errorBody()?.string() ?: "Unknown HTTP error"))
                }

                else -> Response.Error.UnknownError(Throwable("Unknown error"))

            }
        } catch (e: IOException) {
            Response.Error.NetworkError(e)
        } catch (e: Exception) {
            Response.Error.UnknownError(e)
        }
    }
}