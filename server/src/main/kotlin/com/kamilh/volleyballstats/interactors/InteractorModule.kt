package com.kamilh.volleyballstats.interactors

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface InteractorModule {

    val DelayedSynchronizeScheduler.bind: SynchronizeScheduler
        @Provides get() = this

    val UpdateMatchesInteractor.bind: UpdateMatches
        @Provides get() = this

    val UpdatePlayersInteractor.bind: UpdatePlayers
        @Provides get() = this

    val UpdateTeamsInteractor.bind: UpdateTeams
        @Provides get() = this

    val UpdateToursInteractor.bind: UpdateTours
        @Provides get() = this

    val MatchReportPreparerInteractor.bind: MatchReportPreparer
        @Provides get() = this

    val UpdateMatchReportInteractor.bind: UpdateMatchReports
        @Provides get() = this

    val FixWrongPlayersInteractor.bind: FixWrongPlayers
        @Provides get() = this

    val SynchronizeStateHolder.bindSender: SynchronizeStateSender
        @Provides get() = this

    val SynchronizeStateHolder.bindReceiver: SynchronizeStateReceiver
        @Provides get() = this

    val GetMatchReportInteractor.bindReceiver: GetMatchReport
        @Provides get() = this

    val CacheInvalidatorInteractor.bindReceiver: CacheInvalidator
        @Provides get() = this
}
