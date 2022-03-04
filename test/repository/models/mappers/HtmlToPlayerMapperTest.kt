package com.kamilh.repository.models.mappers

import com.kamilh.models.Result
import com.kamilh.repository.parsing.JsoupHtmlParser
import org.junit.Test
import repository.parsing.EmptyResultException

class HtmlToPlayerMapperTest {

    private val mapper = HtmlToPlayerMapper(htmlParser = JsoupHtmlParser())

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
    fun `mapper returns EmptyResultException when searched tag exists but is empty`() {
        // GIVEN
        val html = emptyHtml()

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
        assert(mapped.error.exception is EmptyResultException)
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
}

private fun html(
    idString: String = "0",
    name: String = "",
): String =
    """
<div id="hiddenPlayersListAllBuffer">
    <div class="item-1 col-xs-6 col-sm-4 col-md-3 col-lg-2 playersItem"  data-fullnamefirstletter="A" >
        <div class="thumbnail player">
            <a href="/statsPlayers/id/$idString.html"><img src="https://dl.siatkarskaliga.pl/39201/inline/scalecrop=400x400/cb6b59/NIMIR.jpg" width="400" height="400" alt="$name" class="img-responsive isphoto" /></a>
            <div class="caption no-overflow">
                <h3><a href="/statsPlayers/id/26813.html">Nimir Abdel-Aziz</a></h3>
                <div class="player-ranks"><div class="block-rank" data-toggle="tooltip" title="Pozycja w rankingu blokujących">510</div><div class="score-rank" data-toggle="tooltip" title="Pozycja w rankingu punktujących">601</div><div class="spike-rank" data-toggle="tooltip" title="Pozycja w rankingu atakujących">619</div><div class="serve-rank" data-toggle="tooltip" title="Pozycja w rankingu zagrywających">422</div></div>
            </div>
        </div>
    </div>
</div>
""".trimIndent()

private fun emptyHtml(): String =
    """
<div id="hiddenPlayersListAllBuffer">
</div>
""".trimIndent()