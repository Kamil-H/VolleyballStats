package com.kamilh.volleyballstats.network

import com.kamilh.volleyballstats.domain.models.Error
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.utils.io.errors.*

sealed class NetworkError(override val message: String) : Error {

    class ConnectionError(val ioException: IOException) : NetworkError("ConnectionError")

    class UnexpectedException(val throwable: Throwable) : NetworkError("UnexpectedException")

    sealed class HttpError(override val message: String) : NetworkError(message) {

        object BadRequestException : HttpError("BadRequestException")
        object UnauthorizedException : HttpError("UnauthorizedException")
        object ForbiddenException : HttpError("ForbiddenException")
        object NotFoundException : HttpError("NotFoundException")
        object InternalServerErrorException : HttpError("InternalServerErrorException")
        object MethodNotAllowed: HttpError("MethodNotAllowed")
        object NotAcceptable: HttpError("NotAcceptable")
        object ProxyAuthenticationRequired: HttpError("ProxyAuthenticationRequired")
        object RequestTimeout: HttpError("RequestTimeout")
        object Conflict: HttpError("Conflict")
        class Other(val status: Int): HttpError("Other(status: $status)")
        class UnexpectedException(val responseException: ResponseException?) : HttpError(
            "UnexpectedException(responseException: $responseException)"
        )

        companion object {
            fun handle(responseException: ResponseException): HttpError =
                handle(status = responseException.response.status, responseException = responseException)

            fun handle(status: HttpStatusCode, responseException: ResponseException? = null): HttpError =
                when (status.value) {
                    400 -> BadRequestException
                    401 -> UnauthorizedException
                    403 -> ForbiddenException
                    404 -> NotFoundException
                    405 -> MethodNotAllowed
                    406 -> NotAcceptable
                    407 -> ProxyAuthenticationRequired
                    408 -> RequestTimeout
                    409 -> Conflict
                    in 410..499 -> Other(status.value)
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
