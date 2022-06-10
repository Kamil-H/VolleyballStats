package com.kamilh.volleyballstats.repository.models.mappers

import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.parsePolishLeagueDate
import com.kamilh.volleyballstats.domain.models.PlayerId
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.utils.CurrentDate
import com.kamilh.volleyballstats.models.PlayerDetails
import com.kamilh.volleyballstats.models.PlayerWithDetails
import com.kamilh.volleyballstats.models.TeamPlayer
import com.kamilh.volleyballstats.repository.parsing.HtmlParser
import com.kamilh.volleyballstats.repository.parsing.ParseResult
import me.tatarka.inject.annotations.Inject

/**
<div class="col-xs-9 col-sm-8 col-md-8 col-lg-9">
    <h1>Robbert Andringa</h1>
        <div class="playerteamname" style="margin-bottom:20px;">
            <a href='/teams/id/1406.html'> <span>Indykpol AZS Olsztyn</span>
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
        <div class="col-sm-4 col-md-4 col-lg-3 col-sm-offset-2 col-lg-offset-3"><div class="datainfo small">Data urodzenia:<span> 28.10.1990</span></div></div>
        <div class="col-sm-5 col-md-4"><div class="datainfo small">Specjalność: <span>Przyjmujący</span></div></div>
    </div>
<hr />
<div class="row">
    <div class="col-sm-3 col-md-3 col-lg-3 col-lg-offset-1"><div class="datainfo text-center">Wzrost:<span> 191</span></div></div>
    <div class="col-sm-3 col-md-3 col-lg-3"><div class="datainfo text-center">Waga:<span> 86</span></div></div>
    <div class="col-sm-6 col-md-6 col-lg-4"><div class="datainfo text-center">Zasięg z wyskoku do ataku:<span> 330</span></div></div>
</div>
<hr />
</div>
    <div class="col-sm-4 col-md-4 col-lg-3 player">
        <a href="https://dl.siatkarskaliga.pl/459300/inline/xy=1200x1200/149648/6_andringa.png" data-gallery>
        <img class="img-responsive" src="https://dl.siatkarskaliga.pl/459300/inline/scalecrop=400x800/1c0550/6_andringa.png" title="Robbert Andringa" alt="Robbert Andringa" rel="https://dl.siatkarskaliga.pl/459300/inline/0593d2/6_andringa.png" style="cursor: pointer;" />
        </a>
        <div class="playernumber">Numer<span>6</span></div>
    </div>
</div>
<div class="clear">&nbsp;</div>
<div class="space">&nbsp;</div>
<a class="btn btn-default" href="/players.html">powrót</a>
<a class="btn btn-default" style="float:right;" href='/statsPlayers/id/30339.html'>Statystyki zawodnika</a>
 */
@Inject
class HtmlToPlayerWithDetailsMapper(private val htmlParser: HtmlParser) : HtmlMapper<PlayerWithDetails> {

    override fun map(html: String): ParseResult<PlayerWithDetails> = htmlParser.parse(html) {
        var date: LocalDate? = null
        var height: Int? = null
        var weight: Int? = null
        var range: Int? = null
        var number: Int? = null
        var id: PlayerId? = null
        var name: String? = null
        var team: TeamId? = null
        var specialization: Specialization? = null
        getElementsByClass("playerteamname").forEach {
            team = TeamId(it.select("a").attr("href").extractTeamId()!!)
        }
        select("h1").forEach {
            name = it.html()
        }
        getElementsByClass("btn btn-default").forEach { element ->
            element.attr("href").extractPlayerId()?.let {
                id = PlayerId(it)
            }
        }
        getElementsByClass("datainfo small").forEach {
            val dateRegex = Regex("\\d{2}.\\d{2}.\\d{4}")
            it.children().forEach { child ->
                val outerHtml = child.outerHtml()
                dateRegex.find(outerHtml)?.value?.let { dateString ->
                    date = LocalDate.parsePolishLeagueDate(dateString)
                }
                outerHtml.tryCreateSpecialization()?.let {
                    specialization = it
                }
            }
        }
        val regex = Regex("(\\d+)")
        getElementsByClass("datainfo text-center").forEach {
            val childText = it.outerHtml()
            val value = regex.find(childText)?.value?.toIntOrNull()
            if (value != null) {
                when {
                    childText.contains("wzrost", ignoreCase = true) -> height = value
                    childText.contains("waga", ignoreCase = true) -> weight = value
                    childText.contains("zasięg", ignoreCase = true) -> range = value
                }
            }
        }
        getElementsByClass("playernumber").forEach {
            number = regex.find(it.outerHtml())?.value?.toIntOrNull()
        }
        PlayerWithDetails(
            teamPlayer = TeamPlayer(
                id = id!!,
                name = name!!,
                imageUrl = null,
                team = team!!,
                specialization = specialization!!,
                updatedAt = CurrentDate.localDateTime,
            ),
            details = PlayerDetails(
                date = date!!,
                height = height,
                weight = weight,
                range = range,
                number = number!!,
                updatedAt = CurrentDate.localDateTime,
            )
        )
    }

    private fun String.tryCreateSpecialization(): Specialization? =
        when {
            contains("Rozgrywający") -> Specialization.Setter
            contains("Przyjmujący") -> Specialization.OutsideHitter
            contains("Atakujący") -> Specialization.OppositeHitter
            contains("Libero") -> Specialization.Libero
            contains("Środkowy") -> Specialization.MiddleBlocker
            else -> null
        }
}