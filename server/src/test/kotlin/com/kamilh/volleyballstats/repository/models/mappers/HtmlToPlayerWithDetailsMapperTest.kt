package com.kamilh.volleyballstats.repository.models.mappers

import com.kamilh.volleyballstats.models.Result
import com.kamilh.volleyballstats.models.TeamPlayer
import com.kamilh.volleyballstats.repository.parsing.JsoupHtmlParser
import org.junit.Test
import com.kamilh.volleyballstats.datetime.LocalDate

class HtmlToPlayerWithDetailsMapperTest {

    private val mapper = HtmlToPlayerWithDetailsMapper(htmlParser = JsoupHtmlParser())

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
        assert(mapped.value.details.date == date)
        assert(mapped.value.details.height == height)
        assert(mapped.value.details.weight == weight)
        assert(mapped.value.details.range == range)
        assert(mapped.value.details.number == number)
    }

    @Test
    fun `when height is empty Success with null`() {
        // GIVEN
        val html = html(height = "")

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        assert(mapped.value.details.height == null)
    }

    @Test
    fun `when weight is empty Success with null`() {
        // GIVEN
        val html = html(weight = "")

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        assert(mapped.value.details.weight == null)
    }

    @Test
    fun `when range is empty Success with null`() {
        // GIVEN
        val html = html(range = "")

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        assert(mapped.value.details.range == null)
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

    @Test
    fun `test if when id is correct then result is Success and id is parsed properly`() {
        // GIVEN
        val id = 1101312L

        val html = html(idString = id.toString())

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        val first = mapped.value.teamPlayer
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
        val first = mapped.value.teamPlayer
        assert(first.name == name)
    }

    @Test
    fun `url is always null`() {
        // GIVEN
        val html = html()

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        val first = mapped.value.teamPlayer
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
        val first = mapped.value.teamPlayer
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

        val html = html(specialization = specialization.name)

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        val first = mapped.value.teamPlayer
        assert(first.specialization == specialization)
    }

    @Test
    fun `test if when positionId is empty String then result is Failure`() {
        // GIVEN
        val idString = ""

        val html = html(specialization = idString)

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }

    @Test
    fun `test if when positionId is not a number String then result is Failure`() {
        // GIVEN
        val idString = "text"

        val html = html(specialization = idString)

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
    idString: String = "0",
    name: String = "",
    teamIdString: String = "0",
    specialization: String = "Przyjmujący",
): String =
"""
<div class="col-xs-9 col-sm-8 col-md-8 col-lg-9">
    <h1>$name</h1>
        <div class="playerteamname" style="margin-bottom:20px;">
            <a href='/teams/id/$teamIdString.html'> <span>Indykpol AZS Olsztyn</span>
        </a>
    </div>
</div>
<div class="col-xs-3 col-sm-4 col-md-4 col-lg-3 text-right">
        <a style="position:relative;top:-15px;" href='/teams/id/1406.html'>
            <img src="https://dl.siatkarskaliga.pl/411000/inline/scalecrop=200x200/34052d/indykpol_olsztyn.png" alt="" class="img-responsive playerteamlogo" />
        </a>
    </div>
</div>
<hr style="margin-top:0;" />
    <div class="row">
        <div class="col-sm-4 col-md-4 col-lg-3 col-sm-offset-2 col-lg-offset-3"><div class="datainfo small">Data urodzenia:<span> $dateString</span></div></div>
        <div class="col-sm-5 col-md-4"><div class="datainfo small">Specjalność: <span>$specialization</span></div></div>
    </div>
<hr />
<div class="row">
    <div class="col-sm-3 col-md-3 col-lg-3 col-lg-offset-1"><div class="datainfo text-center">Wzrost:<span> $height</span></div></div>
    <div class="col-sm-3 col-md-3 col-lg-3"><div class="datainfo text-center">Waga:<span> $weight</span></div></div>
    <div class="col-sm-6 col-md-6 col-lg-4"><div class="datainfo text-center">Zasięg z wyskoku do ataku:<span> $range</span></div></div>
</div>
<hr />
</div>
    <div class="col-sm-4 col-md-4 col-lg-3 player">
        <a href="https://dl.siatkarskaliga.pl/459300/inline/xy=1200x1200/149648/6_andringa.png" data-gallery>
        <img class="img-responsive" src="https://dl.siatkarskaliga.pl/459300/inline/scalecrop=400x800/1c0550/6_andringa.png" title="Robbert Andringa" alt="Robbert Andringa" rel="https://dl.siatkarskaliga.pl/459300/inline/0593d2/6_andringa.png" style="cursor: pointer;" />
        </a>
        <div class="playernumber">Numer<span>$number</span></div>
    </div>
</div>
<div class="clear">&nbsp;</div>
<div class="space">&nbsp;</div>
<a class="btn btn-default" href="/players.html">powrót</a>
<a class="btn btn-default" style="float:right;" href='/statsPlayers/id/$idString.html'>Statystyki zawodnika</a>
""".trimIndent()