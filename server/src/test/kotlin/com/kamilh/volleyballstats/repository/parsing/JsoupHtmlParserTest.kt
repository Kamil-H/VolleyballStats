package com.kamilh.volleyballstats.repository.parsing

import com.kamilh.volleyballstats.domain.models.Result
import org.jsoup.nodes.Document
import org.junit.Test

class JsoupHtmlParserTest {

    private val parser = JsoupHtmlParser()

    @Test
    fun `test that parser returns Success when returned value is not null`() {
        // GIVEN
        val html = ""
        val parseResult = ""
        val mapper: Document.() -> String? = { parseResult }

        // WHEN
        val mapped = parser.parse(html, mapper)

        // THEN
        require(mapped is Result.Success)
        assert(mapped.value == parseResult)
    }

    @Test
    fun `test that parser returns Failure when returned value is null`() {
        // GIVEN
        val html = ""
        val mapper: Document.() -> String? = { null }

        // WHEN
        val mapped = parser.parse(html, mapper)

        // THEN
        require(mapped is Result.Failure)
        require(mapped.error is ParseError.Html)
    }

    @Test
    fun `test that parser returns Failure when exception is thrown`() {
        // GIVEN
        val html = ""
        val mapper: Document.() -> String? = { null }

        // WHEN
        val mapped = parser.parse(html, mapper)

        // THEN
        require(mapped is Result.Failure)
        require(mapped.error is ParseError.Html)
    }
}