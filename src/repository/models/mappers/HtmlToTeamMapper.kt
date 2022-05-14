package com.kamilh.repository.models.mappers

import com.kamilh.models.Team
import com.kamilh.models.TeamId
import com.kamilh.models.Url
import com.kamilh.repository.parsing.HtmlParser
import com.kamilh.utils.CurrentDate
import me.tatarka.inject.annotations.Inject
import com.kamilh.repository.parsing.EmptyResultException
import com.kamilh.repository.parsing.ParseResult

/**
<div class="thumbnail teamlist">
    <a href="/teams/id/30288.html"><img src="https://dl.siatkarskaliga.pl/415936/inline/crop=0x175x5293x2828,scalecrop=600x300/8834bf/Grupowe.jpg" width="600" height="300" alt="Aluron CMC Warta Zawiercie" class="img-responsive"></a>
        <div class="caption">
            <h3 style="height:auto;"><a href="/teams/id/30288.html">Aluron CMC Warta Zawiercie</a></h3>
            <p>ul. Blanowska 40, Zawiercie<br><a href="http://aluroncmc.pl" rel="external nofollow">http://aluroncmc.pl</a><br><a href="mailto:klub@aluroncmc.pl">klub@aluroncmc.pl</a></p>
            <div class="teamlistlogo">
            <a href="/teams/id/30288.html"><img src="https://dl.siatkarskaliga.pl/412889/inline/scalecrop=100x100/d377f7/aluron_cmc_logo_2020.png" alt="Aluron CMC Warta Zawiercie" class="img-responsive"></a>
        </div>
    </div>
</div>
 */
@Inject
class HtmlToTeamMapper(private val htmlParser: HtmlParser) : HtmlMapper<List<Team>> {

    override fun map(html: String): ParseResult<List<Team>> = htmlParser.parse(html) {
        getElementsByClass("thumbnail teamlist").flatMap {
            val teamImageUrl = it.select("img").attr("src")
            val teamListLogo = it.getElementsByClass("teamlistlogo")
            teamListLogo.map { element ->
                val a = element.select("a")
                val id = a.attr("href").extractTeamId()

                val img = element.select("img")
                val image = img.attr("src")
                val name = img.attr("alt")

                Team(
                    id = TeamId(id!!),
                    name = name,
                    teamImageUrl = Url.createOrNull(teamImageUrl)!!,
                    logoUrl = Url.createOrNull(image)!!,
                    updatedAt = CurrentDate.localDateTime,
                )
            }
        }.apply {
            if (isEmpty()) {
                throw EmptyResultException()
            }
        }
    }
}