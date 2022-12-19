package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.matchIdOf
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.tourIdOf
import com.kamilh.volleyballstats.domain.tourOf
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.storage.MatchStorage
import com.kamilh.volleyballstats.storage.TourStorage
import com.kamilh.volleyballstats.storage.matchStorageOf
import com.kamilh.volleyballstats.storage.tourStorageOf
import com.kamilh.volleyballstats.utils.testAppDispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class CacheInvalidatorTest {

    private fun interactor(
        appDispatchers: AppDispatchers = testAppDispatchers,
        getMatchReport: GetMatchReport = getMatchReportOf(),
        matchStorage: MatchStorage = matchStorageOf(),
        tourStorage: TourStorage = tourStorageOf(),
    ): CacheInvalidatorInteractor = CacheInvalidatorInteractor(
        appDispatchers = appDispatchers,
        getMatchReport = getMatchReport,
        matchStorage = matchStorage,
        tourStorage = tourStorage,
    )

    @Test
    fun `interactor asks for all of the matchIds`() = runTest {
        // GIVEN
        val tours = (0..1).map { tourOf(id = tourIdOf(it.toLong())) }
        val matchIds = (0..3).map { matchIdOf(it.toLong()) }

        val askedMatchIds = mutableListOf<MatchId>()
        val interactor = interactor(
            getMatchReport = getMatchReportOf {
                askedMatchIds.add(it)
                null
            },
            matchStorage = matchStorageOf(
                getMatchIdsWithReport = flowOf(matchIds)
            ),
            tourStorage = tourStorageOf(getAll = flowOf(tours))
        )

        // WHEN
        interactor()

        // THEN
        val reversed = matchIds.reversed()
        assertEquals(expected = reversed + reversed, actual = askedMatchIds)
    }
}
