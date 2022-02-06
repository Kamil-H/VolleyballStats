package com.kamilh.repository.models.mappers

import com.kamilh.models.Result
import com.kamilh.repository.parsing.JsoupHtmlParser
import org.junit.Test
import java.time.LocalDate

class HtmlToTeamPlayerMapperTest {

    private val mapper = HtmlToPlayerDetailsMapper(htmlParser = JsoupHtmlParser())

    @Test
    fun `test if when html is empty then Failure is returned`() {
        // GIVEN
        val html = ""

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }

    @Test
    fun `when all values are correct Succes is returned`() {
        // GIVEN
        val date = LocalDate.of(1992, 5, 6)
        val dateString = "06.05.1992"
        val height = 200
        val weight = 90
        val range = 349
        val number = 1

        val html = html(
            dateString = dateString,
            height = height.toString(),
            weight = weight.toString(),
            range = range.toString(),
            number = number.toString(),
        )

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        assert(mapped.value.date == date)
        assert(mapped.value.height == height)
        assert(mapped.value.weight == weight)
        assert(mapped.value.range == range)
        assert(mapped.value.number == number)
    }

    @Test
    fun `when height is empty Success with null`() {
        // GIVEN
        val html = html(height = "")

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        assert(mapped.value.height == null)
    }

    @Test
    fun `when weight is empty Success with null`() {
        // GIVEN
        val html = html(weight = "")

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        assert(mapped.value.weight == null)
    }

    @Test
    fun `when range is empty Success with null`() {
        // GIVEN
        val html = html(range = "")

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        assert(mapped.value.range == null)
    }

    @Test
    fun `when number is empty Failure is returned`() {
        // GIVEN
        val html = html(number = "")

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }

    @Test
    fun `when dateString is empty Failure is returned`() {
        // GIVEN
        val html = html(dateString = "")

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }
}

private fun html(
    dateString: String = "06.05.1992",
    height: String = "0",
    weight: String = "0",
    range: String = "349",
    number: String = "0",
): String =
"""
/**
<div class="row">
    <div class="col-sm-4 col-md-4 col-lg-3 col-sm-offset-2 col-lg-offset-3"><div class="datainfo small">Data urodzenia:<span> $dateString</span></div></div>
    <div class="col-sm-5 col-md-4"><div class="datainfo small">Specjalność: <span> Atakujący</span></div></div>
</div>
(...)
<div class="row">
    <div class="col-sm-3 col-md-3 col-lg-3 col-lg-offset-1"><div class="datainfo text-center">Wzrost:<span> $height</span></div></div>
    <div class="col-sm-3 col-md-3 col-lg-3"><div class="datainfo text-center">Waga:<span> $weight</span></div></div>
    <div class="col-sm-6 col-md-6 col-lg-4"><div class="datainfo text-center">Zasięg z wyskoku do ataku:<span> $range</span></div></div>
</div>
(...)
<div class="playernumber">Numer<span>$number</span></div>
 */
""".trimIndent()