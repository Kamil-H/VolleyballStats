package com.kamilh.volleyballstats.repository.models.mappers

import com.kamilh.volleyballstats.domain.models.Result
import org.junit.Test

class HtmlToMatchReportIdTest {

    private val mapper = HtmlToMatchReportId()

    @Test
    fun `test if when html contains correct id then Success is returned and id is correct`() {
        // GIVEN
        val id = 2103711L
        val html = html(idString = id.toString())

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
    }

    @Test
    fun `test if when html contains empty id then Failure is returned`() {
        // GIVEN
        val idString = ""
        val html = html(idString = idString)

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }

    @Test
    fun `test if when html contains text id then Failure is returned`() {
        // GIVEN
        val idString = "text"
        val html = html(idString = idString)

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }

    @Test
    fun `test if when html is empty Failure is returned`() {
        // GIVEN
        val html = ""

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }
}

private fun html(idString: String): String =
    """
    <a class="btn btn-default btm-margins" href="https://www.plusliga.pl/games/action/downloadStats/matchId/2103711.html">Statystyki meczu</a>
    <iframe class="widget-ppp widget-pbp" src="https://widgets.volleystation.com/app/widget/play-by-play/$idString?home_image=https://dl.siatkarskaliga.pl/410999/inline/scalecrop=250x250/182736/Grupa-Azoty-ZAKSA-K%C4%99dzierzyn-Ko%C5%BAle_logotyp.png&away_image=https://dl.siatkarskaliga.pl/412482/inline/scalecrop=250x250/d8f9c2/slepsk.png&side_force=home"></iframe>
    """.trimIndent()