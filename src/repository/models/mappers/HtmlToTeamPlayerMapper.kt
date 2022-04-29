package com.kamilh.repository.models.mappers

import com.kamilh.models.PlayerId
import com.kamilh.models.TeamId
import com.kamilh.models.TeamPlayer
import com.kamilh.models.Url
import com.kamilh.repository.parsing.HtmlParser
import com.kamilh.utils.CurrentDate
import me.tatarka.inject.annotations.Inject
import repository.parsing.EmptyResultException
import repository.parsing.ParseResult

/**
<div class="item-1 col-xs-6 col-sm-4 col-md-3 col-lg-2 playersItem" data-playerposition="4" data-fullnamefirstletter="A" data-teamsid="1407">
    <div class="thumbnail player">
        <a href="/players/id/27975.html"><img src="https://dl.siatkarskaliga.pl/415488/inline/crop=226x30x411x215,scalecrop=400x400/51db60/Sebastian%20Adamczyk.png" width="400" height="400" alt="Sebastian Adamczyk" class="img-responsive isphoto" /></a>
        <div class="caption">
        <h3><a href="/players/id/27975.html">Sebastian Adamczyk</a></h3>
        </div>
        <div class="team-logo"><img title="PGE Skra Bełchatów" alt="PGE Skra Bełchatów" src="https://dl.siatkarskaliga.pl/411003/inline/scalecrop=60x60/b5d404/skra.png"></div>
        <div class="playerposition">środkowy</div>
    </div>
</div>
 */
@Inject
class HtmlToTeamPlayerMapper(private val htmlParser: HtmlParser) : HtmlMapper<List<TeamPlayer>> {

    override fun map(html: String): ParseResult<List<TeamPlayer>> = htmlParser.parse(html) {
        getElementById("hiddenPlayersListAllBuffer").children().map {
            val positionId = it.attr("data-playerposition").toInt()
            val teamId = it.attr("data-teamsid").toLong()
            val thumbnailPlayer = it.getElementsByClass("thumbnail player")

            val id = thumbnailPlayer.select("a").attr("href")
            val image = thumbnailPlayer.select("img")
            val imageUrl = image.attr("src")
            val name = image.attr("alt")

            TeamPlayer(
                id = PlayerId(id.extractPlayerId()!!),
                name = name,
                imageUrl = Url.createOrNull(imageUrl),
                team = TeamId(teamId),
                specialization = TeamPlayer.Specialization.create(positionId),
                updatedAt = CurrentDate.localDateTime,
            )
        }.apply {
            if (isEmpty()) {
                throw EmptyResultException()
            }
        }
    }
}