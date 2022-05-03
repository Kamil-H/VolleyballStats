package routes

import com.kamilh.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

typealias CallResult<T> = Result<T, CallError>

data class CallError(
    val status: HttpStatusCode,
    override val message: String? = null,
) : Error {

    companion object {

        fun resourceNotFound(subject: String, id: String): CallError =
            CallError(
                status = HttpStatusCode.NotFound,
                message = "A $subject with the ID $id could not be found."
            )

        fun missingParameter(queryParam: String): CallError =
            CallError(
                status = HttpStatusCode.BadRequest,
                message = "The parameter [$queryParam] is required."
            )

        fun missingParameterInPath(): CallError =
            CallError(
                status = HttpStatusCode.BadRequest,
                message = "The parameter is missing in the path."
            )

        fun wrongParameterType(param: String, correctType: String): CallError =
            CallError(
                status = HttpStatusCode.BadRequest,
                message = "Passed [$param] is of wrong type. It is suppose to be $correctType."
            )

        fun wrongTourId(tourId: TourId): CallError =
            resourceNotFound(subject = "Tour", id = tourId.value.toString())
    }
}

suspend inline fun ApplicationCall.respond(callResult: CallResult<*>) {
    callResult
        .onSuccess { value ->
            if (value != null) {
                respond(HttpStatusCode.OK, value)
            } else {
                respond(HttpStatusCode.OK)
            }
        }.onFailure { error ->
            if (error.message != null) {
                respond(error.status, error.message)
            } else {
                respond(error.status)
            }
        }
}