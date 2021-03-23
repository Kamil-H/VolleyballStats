package com.kamilh.models

import io.ktor.client.features.*
import io.ktor.utils.io.errors.*

sealed class NetworkError(override val message: String? = null) : Error {

    class ConnectionError(val ioException: IOException) : NetworkError()

    class UnexpectedException(val throwable: Throwable) : NetworkError()

    sealed class HttpError : NetworkError() {

        object BadRequestException : HttpError()
        object UnauthorizedException : HttpError()
        object ForbiddenException : HttpError()
        object NotFoundException : HttpError()
        object InternalServerErrorException : HttpError()
        object MethodNotAllowed: HttpError()
        object NotAcceptable: HttpError()
        object ProxyAuthenticationRequired: HttpError()
        object RequestTimeout: HttpError()
        object Conflict: HttpError()
        class Other(val status: Int): HttpError()
        class UnexpectedException(val responseException: ResponseException) : HttpError()

        companion object {
            fun handle(responseException: ResponseException): HttpError =
                when (responseException.response.status.value) {
                    400 -> BadRequestException
                    401 -> UnauthorizedException
                    403 -> ForbiddenException
                    404 -> NotFoundException
                    405 -> MethodNotAllowed
                    406 -> NotAcceptable
                    407 -> ProxyAuthenticationRequired
                    408 -> RequestTimeout
                    409 -> Conflict
                    in 410..499 -> Other(responseException.response.status.value)
                    in 500..599 -> InternalServerErrorException
                    else -> UnexpectedException(responseException)
                }
        }
    }

    companion object {
        fun createFrom(throwable: Throwable): NetworkError =
            when (throwable) {
                is ResponseException -> HttpError.handle(throwable)
                is IOException -> ConnectionError(throwable)
                else -> UnexpectedException(throwable)
            }
    }
}
