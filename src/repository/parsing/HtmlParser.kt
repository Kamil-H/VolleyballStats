package com.kamilh.repository.parsing

import com.kamilh.models.Result
import me.tatarka.inject.annotations.Inject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

interface HtmlParser {
    fun <T> parse(html: String, mapper: Document.() -> T?): ParseResult<T>
}

@Inject
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