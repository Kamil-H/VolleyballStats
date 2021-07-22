package repository.models.mappers

import com.kamilh.models.Result
import com.kamilh.repository.models.mappers.HtmlToTeamMapper
import com.kamilh.repository.parsing.JsoupHtmlParser
import org.junit.Test

class HtmlToTeamMapperTest {

    private val mapper = HtmlToTeamMapper(htmlParser = JsoupHtmlParser())

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
    fun `test if when teamImageUrl is correct then result is Success and imageUrl is parsed properly`() {
        // GIVEN
        val imageUrl = "https://google.com"

        val html = html(teamImageUrl = imageUrl)

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        val first = mapped.value.first()
        assert(first.teamImageUrl.value == imageUrl)
    }

    @Test
    fun `test if when teamImageUrl is empty then result is Failure`() {
        // GIVEN
        val imageUrl = ""

        val html = html(teamImageUrl = imageUrl)

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }

    @Test
    fun `test if when logoUrl is correct then result is Success and imageUrl is parsed properly`() {
        // GIVEN
        val imageUrl = "https://google.com"

        val html = html(logoUrl = imageUrl)

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        val first = mapped.value.first()
        assert(first.logoUrl.value == imageUrl)
    }

    @Test
    fun `test if when logoUrl is empty then result is Failure`() {
        // GIVEN
        val imageUrl = ""

        val html = html(logoUrl = imageUrl)

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }
}

private fun html(
    idString: String = "0",
    name: String = "",
    teamImageUrl: String = "google.com",
    logoUrl: String = "google.com",
): String =
    """
<div class="thumbnail teamlist">
    <a href="/teams/id/30288.html"><img src="$teamImageUrl" width="600" height="300" alt="Aluron CMC Warta Zawiercie" class="img-responsive"></a>
        <div class="caption">
            <h3 style="height:auto;"><a href="/teams/id/30288.html">Aluron CMC Warta Zawiercie</a></h3>
            <p>ul. Blanowska 40, Zawiercie<br><a href="http://aluroncmc.pl" rel="external nofollow">http://aluroncmc.pl</a><br><a href="mailto:klub@aluroncmc.pl">klub@aluroncmc.pl</a></p>
            <div class="teamlistlogo">
            <a href="/teams/id/$idString.html"><img src="$logoUrl" alt="$name" class="img-responsive"></a>
        </div>
    </div>
</div>
""".trimIndent()