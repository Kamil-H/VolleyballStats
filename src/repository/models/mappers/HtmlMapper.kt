package com.kamilh.repository.models.mappers

import repository.parsing.ParseResult

fun interface HtmlMapper<T> {

    fun map(html: String): ParseResult<T>
}