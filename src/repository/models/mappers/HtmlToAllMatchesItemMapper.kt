package com.kamilh.repository.models.mappers

import com.kamilh.datetime.ZonedDateTime
import com.kamilh.models.MatchInfo
import com.kamilh.models.MatchId
import com.kamilh.models.TeamId
import com.kamilh.repository.extensions.toPolishLeagueLocalDate
import com.kamilh.repository.parsing.HtmlParser
import repository.parsing.ParseResult

/**
 * <div class="gameresult clickable" onclick="location.href='/games/id/1101019.html';"> <span class="green">3</span><span class="doubledot">:</span><span class="red">0</span></div>
 */
class HtmlToAllMatchesItemMapper(private val htmlParser: HtmlParser) : HtmlMapper<List<MatchInfo>> {

    override fun map(html: String): ParseResult<List<MatchInfo>> = htmlParser.parse(html) {
        val matchInfos = mutableListOf<MatchInfo>()
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
                val matchInfo = when {
                    isPotentiallyFinished -> MatchInfo.PotentiallyFinished(
                        id = matchId,
                        date = date!!,
                        home = homeTeamId!!,
                        away = awayTeamId!!,
                    )
                    date == null || date.isMidnight() -> MatchInfo.NotScheduled(
                        id = matchId,
                        date = date,
                        home = homeTeamId!!,
                        away = awayTeamId!!,
                    )
                    else -> MatchInfo.Scheduled(
                        id = matchId,
                        date = date,
                        home = homeTeamId!!,
                        away = awayTeamId!!,
                    )
                }
                matchInfos.add(matchInfo)
            }
        matchInfos
    }

    private fun ZonedDateTime.isMidnight(): Boolean = hour == 0 && minute == 0

    private fun String.dropUntilGameIdUrl(): String =
        dropWhile { it != '\'' }.dropLastWhile { it != '\'' }.replace("\'", "")
}