package com.kamilh.volleyballstats.repository.models.mappers

import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.MatchInfo
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.repository.extensions.toPolishLeagueLocalDate
import com.kamilh.volleyballstats.repository.parsing.HtmlParser
import me.tatarka.inject.annotations.Inject
import com.kamilh.volleyballstats.repository.parsing.ParseResult

/**
 * <div class="gameresult clickable" onclick="location.href='/games/id/1101019.html';"> <span class="green">3</span><span class="doubledot">:</span><span class="red">0</span></div>
 */
@Inject
class HtmlToAllMatchesItemMapper(private val htmlParser: HtmlParser) : HtmlMapper<List<MatchInfo>> {

    override fun map(html: String): ParseResult<List<MatchInfo>> = htmlParser.parse(html) {
        getElementsByClass("row text-center gridtable games alter")
            .mapNotNull { element ->
                var homeTeamId: TeamId? = null
                var awayTeamId: TeamId? = null

                element.getElementsByClass("col-xs-3").forEachIndexed { index, classElement ->
                    val id = classElement.select("a").attr("href").extractTeamId()
                    if (id != null) {
                        val teamId = TeamId(id)
                        when (index) {
                            0 -> homeTeamId = teamId
                            1 -> awayTeamId = teamId
                        }
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
                if (homeTeamId != null && awayTeamId != null) {
                    when {
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
                } else {
                    null
                }
            }
    }

    private fun ZonedDateTime.isMidnight(): Boolean = hour == 0 && minute == 0

    private fun String.dropUntilGameIdUrl(): String =
        dropWhile { it != '\'' }.dropLastWhile { it != '\'' }.replace("\'", "")
}