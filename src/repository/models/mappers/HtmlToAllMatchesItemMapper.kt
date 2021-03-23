package com.kamilh.repository.models.mappers

import com.kamilh.models.AllMatchesItem
import com.kamilh.models.MatchId
import com.kamilh.repository.parsing.HtmlParser
import com.kamilh.scraping.extractGameId
import repository.parsing.ParseResult
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * <div class="gameresult clickable" onclick="location.href='/games/id/1101019.html';"> <span class="green">3</span><span class="doubledot">:</span><span class="red">0</span></div>
 */
class HtmlToAllMatchesItemMapper(private val htmlParser: HtmlParser) {

    fun map(html: String): ParseResult<List<AllMatchesItem>> = htmlParser.parse(html) {
        val allMatchesItems = mutableListOf<AllMatchesItem>()
        getElementsByClass("row text-center gridtable games alter")
            .forEach {
                val dateString = it.getElementsByClass("date khanded").text().replace("TV ", "")
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
                val date = LocalDateTime.parse(dateString, formatter)

                val clickable = it.getElementsByClass("gameresult clickable")
                val id = clickable.attr("onclick").dropUntilGameIdUrl().extractGameId()

                val green = clickable.map { it.getElementsByClass("green").text().toIntOrNull() }.firstOrNull()
                val isPotentiallyFinished = green == 3

                id ?: throw IllegalStateException("Can't extract id from: $clickable")
                date ?: throw IllegalStateException("Can't parse date: $dateString")

                val allMatchesItem = when {
                    isPotentiallyFinished -> AllMatchesItem.PotentiallyFinished(MatchId(id))
                    date.isMidnight() -> AllMatchesItem.NotScheduled(MatchId(id))
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