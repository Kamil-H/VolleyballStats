package routes

import com.kamilh.models.Error
import com.kamilh.models.Result
import com.kamilh.models.onFailure
import com.kamilh.models.onSuccess
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

typealias CallResult<T> = Result<T, CallError>

data class CallError(
    val status: HttpStatusCode,
    override val message: String? = null,
) : Error

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