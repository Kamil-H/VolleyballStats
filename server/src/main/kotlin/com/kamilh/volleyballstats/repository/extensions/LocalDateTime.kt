package com.kamilh.volleyballstats.repository.extensions

import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.datetime.parsePolishLeagueDate

@Suppress("MandatoryBracesIfStatements")
internal fun String.toPolishLeagueLocalDate(): LocalDateTime? =
    if (this.isEmpty()) null else try {
        LocalDateTime.parsePolishLeagueDate(this)
    } catch (exception: Exception) {
        LocalDate.parsePolishLeagueDate(this).atMidnight()
    }
