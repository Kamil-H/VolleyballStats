package com.kamilh.repository.polishleague

import com.kamilh.models.League
import com.kamilh.models.Season
import com.kamilh.models.Tour
import com.kamilh.models.TourId
import java.time.LocalDate
import java.time.LocalDateTime

interface TourCache {

    fun getAll(): List<Tour>
}

/**
 * Simple cache that holds information of the currently supported Tours. It's like that until I find a better way to
 * implement it.
 */
class InMemoryTourCache : TourCache {

    private val tours = listOf(
        Season.create(2020) to LocalDate.of(2020, 9, 11),
        Season.create(2021) to LocalDate.of(2021, 10, 1),
    )

    override fun getAll(): List<Tour> =
        tours.mapIndexed { index, (tour, startDate) ->
            Tour(
                id = TourId(value = index.toLong() + 1),
                name = "PlusLiga",
                season = tour,
                league = League.POLISH_LEAGUE,
                startDate = startDate,
                endDate = null,
                winnerId = null,
                updatedAt = LocalDateTime.now(),
            )
        }
}