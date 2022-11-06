package com.kamilh.volleyballstats.presentation.interactors

import com.kamilh.volleyballstats.domain.utils.Logger
import com.kamilh.volleyballstats.interactors.*
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface InteractorModule {

    @Provides
    fun synchronizeScheduler(): SynchronizeScheduler =
        SynchronizeScheduler { dateTime, _ ->
            Logger.i("Scheduling... $dateTime")
        }

    val UpdateMatchesInteractor.bind: UpdateMatches
        @Provides get() = this

    val UpdatePlayersInteractor.bind: UpdatePlayers
        @Provides get() = this

    val UpdateTeamsInteractor.bind: UpdateTeams
        @Provides get() = this

    val UpdateToursInteractor.bind: UpdateTours
        @Provides get() = this

    val UpdateMatchReportInteractor.bind: UpdateMatchReports
        @Provides get() = this

    val InitializeFiltersInteractor.bind: InitializeFilters
        @Provides get() = this

    val SynchronizeStateHolder.bindSender: SynchronizeStateSender
        @Provides get() = this

    val SynchronizeStateHolder.bindReceiver: SynchronizeStateReceiver
        @Provides get() = this
}
