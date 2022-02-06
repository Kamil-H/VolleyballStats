package com.kamilh.repository.models.mappers

import com.kamilh.extensions.atPolandOffset
import com.kamilh.models.AllMatchesItem
import com.kamilh.models.MatchId
import com.kamilh.models.TeamId
import com.kamilh.repository.extensions.toPolishLeagueLocalDate
import com.kamilh.repository.parsing.HtmlParser
import repository.parsing.ParseResult
import java.time.OffsetDateTime

/**
 * <div class="gameresult clickable" onclick="location.href='/games/id/1101019.html';"> <span class="green">3</span><span class="doubledot">:</span><span class="red">0</span></div>
 */
class HtmlToAllMatchesItemMapper(private val htmlParser: HtmlParser) : HtmlMapper<List<AllMatchesItem>> {

    override fun map(html: String): ParseResult<List<AllMatchesItem>> = htmlParser.parse(html) {
        val allMatchesItems = mutableListOf<AllMatchesItem>()
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
                val date = dateString.toPolishLeagueLocalDate()?.atPolandOffset()

                val clickable = element.getElementsByClass("gameresult clickable")
                val id = clickable.attr("onclick").dropUntilGameIdUrl().extractGameId()

                val green = clickable.map { element.getElementsByClass("green").text().toIntOrNull() }.firstOrNull()
                val isPotentiallyFinished = green == 3

                id ?: error("Can't extract id from: $clickable")

                val matchId = MatchId(id)
                val allMatchesItem = when {
                    isPotentiallyFinished -> AllMatchesItem.PotentiallyFinished(
                        id = matchId,
                        date = date!!,
                        home = homeTeamId!!,
                        away = awayTeamId!!,
                    )
                    date == null || date.isMidnight() -> AllMatchesItem.NotScheduled(
                        id = matchId,
                        date = date,
                        home = homeTeamId!!,
                        away = awayTeamId!!,
                    )
                    else -> AllMatchesItem.Scheduled(
                        id = matchId,
                        date = date,
                        home = homeTeamId!!,
                        away = awayTeamId!!,
                    )
                }
                allMatchesItems.add(allMatchesItem)
            }
        allMatchesItems
    }

    private fun OffsetDateTime.isMidnight(): Boolean = hour == 0 && minute == 0

    private fun String.dropUntilGameIdUrl(): String =
        dropWhile { it != '\'' }.dropLastWhile { it != '\'' }.replace("\'", "")
}