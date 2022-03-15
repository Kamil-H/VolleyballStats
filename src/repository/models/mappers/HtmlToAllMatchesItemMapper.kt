package com.kamilh.repository.models.mappers

import com.kamilh.datetime.ZonedDateTime
import com.kamilh.models.Match
import com.kamilh.models.MatchId
import com.kamilh.models.TeamId
import com.kamilh.repository.extensions.toPolishLeagueLocalDate
import com.kamilh.repository.parsing.HtmlParser
import repository.parsing.ParseResult

/**
 * <div class="gameresult clickable" onclick="location.href='/games/id/1101019.html';"> <span class="green">3</span><span class="doubledot">:</span><span class="red">0</span></div>
 */
class HtmlToAllMatchesItemMapper(private val htmlParser: HtmlParser) : HtmlMapper<List<Match>> {

    override fun map(html: String): ParseResult<List<Match>> = htmlParser.parse(html) {
        val matches = mutableListOf<Match>()
        getElementsByClass("row text-center gridtable games alter")
            .forEach { element ->
                var homeTeamId: TeamId? = null
                var awayTeamId: TeamId? = null

                element.getElementsByClass("col-xs-3").forEachIndexed { index, classElement ->
                    val id = classElement.select("a").attr("href").extractTeamId()
                    val teamId = TeamId(id!!)
                    when (index) {
                        0 -> homeTeamId = teamId
                        1 -> awayTeamId = teamId
                    }
                }

                val dateString = element.getElementsByClass("date khanded").text().replace("TV ", "")
                val date = dateString.toPolishLeagueLocalDate()?.atPolandZone()

                val clickable = element.getElementsByClass("gameresult clickable")
                val id = clickable.attr("onclick").dropUntilGameIdUrl().extractGameId()

                val green = clickable.map { element.getElementsByClass("green").text().toIntOrNull() }.firstOrNull()
                val isPotentiallyFinished = green == 3

                id ?: error("Can't extract id from: $clickable")

                val matchId = MatchId(id)
                val match = when {
                    isPotentiallyFinished -> Match.PotentiallyFinished(
                        id = matchId,
                        date = date!!,
                        home = homeTeamId!!,
                        away = awayTeamId!!,
                    )
                    date == null || date.isMidnight() -> Match.NotScheduled(
                        id = matchId,
                        date = date,
                        home = homeTeamId!!,
                        away = awayTeamId!!,
                    )
                    else -> Match.Scheduled(
                        id = matchId,
                        date = date,
                        home = homeTeamId!!,
                        away = awayTeamId!!,
                    )
                }
                matches.add(match)
            }
        matches
    }

    private fun ZonedDateTime.isMidnight(): Boolean = hour == 0 && minute == 0

    private fun String.dropUntilGameIdUrl(): String =
        dropWhile { it != '\'' }.dropLastWhile { it != '\'' }.replace("\'", "")
}