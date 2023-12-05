package com.kamilh.volleyballstats.repository.polishleague

import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.domain.models.League
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.domain.utils.CurrentDate
import me.tatarka.inject.annotations.Inject

interface TourCache {

    fun getAll(): List<Tour>
}

/**
 * Simple cache that holds information of the currently supported Tours. It's like that until I find a better way to
 * implement it.
 */
@Inject
class InMemoryTourCache : TourCache {

    @Suppress("MagicNumber")
    private val tours = listOf(
        Season.create(2020) to LocalDate.of(2020, 9, 11),
        Season.create(2021) to LocalDate.of(2021, 10, 1),
        Season.create(2022) to LocalDate.of(2022, 9, 30),
        Season.create(2023) to LocalDate.of(2023, 10, 20),
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
                updatedAt = CurrentDate.localDateTime,
            )
        }
}
