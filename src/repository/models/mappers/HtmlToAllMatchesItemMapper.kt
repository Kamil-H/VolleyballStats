package com.kamilh.repository.models.mappers

import com.kamilh.models.AllMatchesItem
import com.kamilh.models.MatchId
import com.kamilh.repository.extensions.toPolishLeagueLocalDate
import com.kamilh.repository.parsing.HtmlParser
import repository.parsing.ParseResult
import java.time.LocalDateTime

/**
 * <div class="gameresult clickable" onclick="location.href='/games/id/1101019.html';"> <span class="green">3</span><span class="doubledot">:</span><span class="red">0</span></div>
 */
class HtmlToAllMatchesItemMapper(private val htmlParser: HtmlParser) : HtmlMapper<List<AllMatchesItem>> {

    override fun map(html: String): ParseResult<List<AllMatchesItem>> = htmlParser.parse(html) {
        val allMatchesItems = mutableListOf<AllMatchesItem>()
        getElementsByClass("row text-center gridtable games alter")
            .forEach {
                println(it.outerHtml() + "\n\n\n")
                val dateString = it.getElementsByClass("date khanded").text().replace("TV ", "")
                val date = dateString.toPolishLeagueLocalDate()

                val clickable = it.getElementsByClass("gameresult clickable")
                val id = clickable.attr("onclick").dropUntilGameIdUrl().extractGameId()

                val green = clickable.map { it.getElementsByClass("green").text().toIntOrNull() }.firstOrNull()
                val isPotentiallyFinished = green == 3

                id ?: error("Can't extract id from: $clickable")

                val allMatchesItem = when {
                    isPotentiallyFinished -> AllMatchesItem.PotentiallyFinished(MatchId(id))
                    date == null || date.isMidnight() -> AllMatchesItem.NotScheduled(MatchId(id))
                    else -> AllMatchesItem.Scheduled(MatchId(id), date)
                }
                allMatchesItems.add(allMatchesItem)
            }
        allMatchesItems
    }

    private fun LocalDateTime.isMidnight(): Boolean = hour == 0 && minute == 0

    private fun String.dropUntilGameIdUrl(): String =
        dropWhile { it != '\'' }.dropLastWhile { it != '\'' }.replace("\'", "")
}