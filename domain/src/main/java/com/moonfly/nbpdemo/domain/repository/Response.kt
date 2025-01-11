package com.moonfly.nbpdemo.domain.repository

sealed class Response<out T> {

    data class Success<T>(val body: T) : Response<T>()

    sealed class Error(open val throwable: Throwable?) : Response<Nothing>() {

        data class HttpError(
            val code: Int,
            override val throwable: Throwable? = null) : Error(throwable)

        data class NetworkError(override val throwable: Throwable? = null) : Error(throwable)

        data class UnknownError(override val throwable: Throwable? = null) : Error(throwable)
    }
}