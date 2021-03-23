package com.kamilh.repository.parsing

import com.kamilh.models.Result
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import repository.parsing.ParseError
import repository.parsing.ParseResult

interface HtmlParser {
    fun <T> parse(html: String, mapper: Document.() -> T?): ParseResult<T>
}

class JsoupHtmlParser : HtmlParser {

    override fun <T> parse(html: String, mapper: Document.() -> T?): ParseResult<T> = try {
        val document = Jsoup.parse(html)
        val t = mapper(document)
        if (t != null) {
            Result.success(t)
        } else {
            Result.failure(
                ParseError.Html(
                    content = html,
                    exception = IllegalStateException("It was impossible to parse HTML file.")
                )
            )
        }
    } catch (exception: Exception) {
        Result.failure(ParseError.Html(html, exception))
    }
}