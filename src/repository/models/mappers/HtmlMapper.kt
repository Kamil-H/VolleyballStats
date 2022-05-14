package com.kamilh.repository.models.mappers

import com.kamilh.repository.parsing.ParseResult

fun interface HtmlMapper<T> {

    fun map(html: String): ParseResult<T>
}