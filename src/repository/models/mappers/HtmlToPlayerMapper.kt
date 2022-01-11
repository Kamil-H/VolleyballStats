package com.kamilh.repository.models.mappers

import com.kamilh.models.Player
import com.kamilh.models.PlayerId
import com.kamilh.models.TeamId
import com.kamilh.models.Url
import com.kamilh.repository.parsing.HtmlParser
import repository.parsing.EmptyResultException
import repository.parsing.ParseResult
import java.time.LocalDateTime

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
class HtmlToPlayerMapper(private val htmlParser: HtmlParser) : HtmlMapper<List<Player>> {

    override fun map(html: String): ParseResult<List<Player>> = htmlParser.parse(html) {
        val elements = getElementById("hiddenPlayersListAllBuffer")
        val players = mutableListOf<Player>()
        elements.children().forEach {
            val positionId = it.attr("data-playerposition").toInt()
            val teamId = it.attr("data-teamsid").toLong()
            val thumbnailPlayer = it.getElementsByClass("thumbnail player")

            val id = thumbnailPlayer.select("a").attr("href")
            val image = thumbnailPlayer.select("img")
            val imageUrl = image.attr("src")
            val name = image.attr("alt")

            players.add(
                Player(
                    id = PlayerId(id.extractPlayerId()!!),
                    name = name,
                    imageUrl = Url.createOrNull(imageUrl),
                    team = TeamId(teamId),
                    specialization = Player.Specialization.create(positionId),
                    updatedAt = LocalDateTime.now(),
                )
            )
        }
        if (players.isEmpty()) {
            throw EmptyResultException()
        }
        players
    }
}