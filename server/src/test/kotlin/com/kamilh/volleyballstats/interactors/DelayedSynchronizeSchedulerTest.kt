package com.kamilh.volleyballstats.interactors

import app.cash.turbine.test
import com.kamilh.volleyballstats.domain.leagueOf
import com.kamilh.volleyballstats.domain.utils.CurrentDate
import com.kamilh.volleyballstats.utils.testClock
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class DelayedSynchronizeSchedulerTest {

    @BeforeTest
    fun setClock() {
        CurrentDate.changeClock(testClock)
    }

    @Test
    fun `schedule emit a value after certain time`() = runTest {
        // GIVEN
        val scheduler = DelayedSynchronizeScheduler(coroutineScope = this)
        val duration = 1.seconds
        val scheduleTime = CurrentDate.zonedDateTime.plus(duration)
        val league = leagueOf()

        // WHEN
        scheduler.schedule(scheduleTime, league)

        // THEN
        scheduler.synchronizeSignal.test {
            assert(awaitItem().league == league)
        }
    }
}
