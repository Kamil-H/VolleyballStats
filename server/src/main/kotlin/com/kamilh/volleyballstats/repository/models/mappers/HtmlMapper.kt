package com.kamilh.volleyballstats.repository.models.mappers

import com.kamilh.volleyballstats.repository.parsing.ParseResult

fun interface HtmlMapper<T> {

    fun map(html: String): ParseResult<T>
}