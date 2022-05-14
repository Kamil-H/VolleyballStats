package com.kamilh.repository.parsing

import com.kamilh.models.Error
import com.kamilh.models.Result

typealias ParseResult<T> = Result<T, ParseError>

sealed class ParseError : Error {
    abstract val content: String
    abstract val exception: Exception

    override val message: String? by lazy { exception.message }

    class Html(override val content: String, override val exception: Exception): ParseError()
    class Json(override val content: String, override val exception: Exception): ParseError()
}

class EmptyResultException(override val message: String? = null): Exception(message)