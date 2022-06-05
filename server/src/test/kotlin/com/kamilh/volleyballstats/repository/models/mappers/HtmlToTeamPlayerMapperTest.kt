package com.kamilh.volleyballstats.repository.models.mappers

import com.kamilh.volleyballstats.domain.models.Result
import com.kamilh.volleyballstats.domain.models.TeamPlayer
import com.kamilh.volleyballstats.repository.parsing.EmptyResultException
import com.kamilh.volleyballstats.repository.parsing.JsoupHtmlParser
import org.junit.Test

class HtmlToTeamPlayerMapperTest {

    private val mapper = HtmlToTeamPlayerMapper(htmlParser = JsoupHtmlParser())

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
    fun `test if when id is correct then result is Success and id is parsed properly`() {
        // GIVEN
        val id = 1101312L

        val html = html(idString = id.toString())

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        val first = mapped.value.first()
        assert(first.id.value == id)
    }

    @Test
    fun `test if when id is empty String then result is Failure`() {
        // GIVEN
        val idString = ""

        val html = html(idString = idString)

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }

    @Test
    fun `test if when id is not a number String then result is Failure`() {
        // GIVEN
        val idString = "text"

        val html = html(idString = idString)

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }

    @Test
    fun `test if when name is correct then result is Success and name is parsed properly`() {
        // GIVEN
        val name = "name"

        val html = html(name = name)

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        val first = mapped.value.first()
        assert(first.name == name)
    }

    @Test
    fun `test if when url is correct then result is Success and imageUrl is parsed properly`() {
        // GIVEN
        val imageUrl = "https://google.com"

        val html = html(imageUrl = imageUrl)

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        val first = mapped.value.first()
        assert(first.imageUrl?.value == imageUrl)
    }

    @Test
    fun `test if when url is empty then result is Success, but imageUrl is null`() {
        // GIVEN
        val imageUrl = ""

        val html = html(imageUrl = imageUrl)

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        val first = mapped.value.first()
        assert(first.imageUrl?.value == null)
    }

    @Test
    fun `test if when teamIdString is correct then result is Success and teamIdString is parsed properly`() {
        // GIVEN
        val teamIdString = 1101312L

        val html = html(teamIdString = teamIdString.toString())

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        val first = mapped.value.first()
        assert(first.team.value == teamIdString)
    }

    @Test
    fun `test if when teamIdString is empty String then result is Failure`() {
        // GIVEN
        val idString = ""

        val html = html(teamIdString = idString)

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }

    @Test
    fun `test if when teamIdString is not a number String then result is Failure`() {
        // GIVEN
        val idString = "text"

        val html = html(teamIdString = idString)

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }

    @Test
    fun `test if when positionId is correct then result is Success and positionId is parsed properly`() {
        // GIVEN
        val specialization = TeamPlayer.Specialization.Libero

        val html = html(positionId = specialization.id.toString())

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        val first = mapped.value.first()
        assert(first.specialization == specialization)
    }

    @Test
    fun `test if when positionId is empty String then result is Failure`() {
        // GIVEN
        val idString = ""

        val html = html(positionId = idString)

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }

    @Test
    fun `test if when positionId is not a number String then result is Failure`() {
        // GIVEN
        val idString = "text"

        val html = html(positionId = idString)

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }

    @Test
    fun `mapper returns EmptyResultException when searched tag exists but is empty`() {
        // GIVEN
        val html = emptyHtml()

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
        assert(mapped.error.exception is EmptyResultException)
    }
}

private fun html(
    idString: String = "0",
    name: String = "",
    imageUrl: String = "google.com",
    teamIdString: String = "0",
    positionId: String = "1",
): String =
"""
<div id="hiddenPlayersListAllBuffer" style="display: none;">
    <div class="item-1 col-xs-6 col-sm-4 col-md-3 col-lg-2 playersItem" data-playerposition="$positionId" data-fullnamefirstletter="A" data-teamsid="$teamIdString">
        <div class="thumbnail player">
            <a href="/players/id/$idString.html"><img src="$imageUrl" width="400" height="400" alt="$name" class="img-responsive isphoto" /></a>
            <div class="caption">
            <h3><a href="/players/id/27975.html">Sebastian Adamczyk</a></h3>
            </div>
            <div class="team-logo"><img title="PGE Skra Bełchatów" alt="PGE Skra Bełchatów" src="https://dl.siatkarskaliga.pl/411003/inline/scalecrop=60x60/b5d404/skra.png"></div>
            <div class="playerposition">środkowy</div>
        </div>
    </div>
</div>
""".trimIndent()

private fun emptyHtml(): String =
"""
<div id="hiddenPlayersListAllBuffer" style="display: none;">
</div>
""".trimIndent()