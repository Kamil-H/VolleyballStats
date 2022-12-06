package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.League
import com.kamilh.volleyballstats.domain.utils.CurrentDate
import com.kamilh.volleyballstats.domain.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
@Singleton
class DelayedSynchronizeScheduler(private val coroutineScope: CoroutineScope) : SynchronizeScheduler {

    private var waitJob: Job? = null

    private val _synchronizeSignal = Channel<SynchronizeSignal>()
    val synchronizeSignal = _synchronizeSignal.receiveAsFlow()

    override fun schedule(dateTime: ZonedDateTime, league: League) {
        if (waitJob?.isActive == true) {
            waitJob?.cancel()
            waitJob = null
        }
        waitJob = coroutineScope.launch {
            val between = dateTime.minus(CurrentDate.zonedDateTime)
            Logger.i("Scheduling... delaying: $between")
            delay(between)
            Logger.i("Scheduling... delayed for: $between")
            _synchronizeSignal.send(SynchronizeSignal(league))
        }
    }

    class SynchronizeSignal(val league: League)
}
