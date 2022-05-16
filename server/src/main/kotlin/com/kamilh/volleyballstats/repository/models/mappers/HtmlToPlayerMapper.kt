package com.kamilh.volleyballstats.repository.models.mappers

import com.kamilh.volleyballstats.models.Player
import com.kamilh.volleyballstats.models.PlayerId
import com.kamilh.volleyballstats.repository.parsing.HtmlParser
import me.tatarka.inject.annotations.Inject
import com.kamilh.volleyballstats.repository.parsing.EmptyResultException
import com.kamilh.volleyballstats.repository.parsing.ParseResult

/**
<div id="hiddenPlayersListAllBuffer">
    <div class="item-1 col-xs-6 col-sm-4 col-md-3 col-lg-2 playersItem"  data-fullnamefirstletter="A" >
        <div class="thumbnail player">
            <a href="/statsPlayers/id/26813.html"><img src="https://dl.siatkarskaliga.pl/39201/inline/scalecrop=400x400/cb6b59/NIMIR.jpg" width="400" height="400" alt="Nimir Abdel-Aziz" class="img-responsive isphoto" /></a>
            <div class="caption no-overflow">
                <h3><a href="/statsPlayers/id/26813.html">Nimir Abdel-Aziz</a></h3>
                <div class="player-ranks"><div class="block-rank" data-toggle="tooltip" title="Pozycja w rankingu blokujących">510</div><div class="score-rank" data-toggle="tooltip" title="Pozycja w rankingu punktujących">601</div><div class="spike-rank" data-toggle="tooltip" title="Pozycja w rankingu atakujących">619</div><div class="serve-rank" data-toggle="tooltip" title="Pozycja w rankingu zagrywających">422</div></div>
            </div>
        </div>
    </div>
</div>
 */
@Inject
class HtmlToPlayerMapper(private val htmlParser: HtmlParser) : HtmlMapper<List<Player>> {

    override fun map(html: String): ParseResult<List<Player>> = htmlParser.parse(html) {
        getElementById("hiddenPlayersListAllBuffer").children().mapNotNull {
            val thumbnailPlayer = it.getElementsByClass("thumbnail player")

            val id = thumbnailPlayer.select("a").attr("href")
            val image = thumbnailPlayer.select("img")
            val name = image.attr("alt")

            Player(
                id = PlayerId(id.extractPlayerId()!!),
                name = name,
            )
        }.apply {
            if (isEmpty()) {
                throw EmptyResultException()
            }
        }
    }
}