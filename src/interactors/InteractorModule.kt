package com.kamilh.interactors

import com.kamilh.utils.Logger
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface InteractorModule {

    @Provides
    fun synchronizeScheduler(): SynchronizeScheduler =
        SynchronizeScheduler {
            Logger.i("Scheduling... $it")
        }

    val UpdateMatchesInteractor.bind: UpdateMatches
        @Provides get() = this

    val UpdatePlayersInteractor.bind: UpdatePlayers
        @Provides get() = this

    val UpdateTeamsInteractor.bind: UpdateTeams
        @Provides get() = this

    val UpdateToursInteractor.bind: UpdateTours
        @Provides get() = this

    val AddUserInteractor.bind: AddUser
        @Provides get() = this

    val GetUserInteractor.bind: GetUser
        @Provides get() = this

    val MatchReportPreparerInteractor.bind: MatchReportPreparer
        @Provides get() = this

    val SubscriptionKeyValidatorInteractor.bind: SubscriptionKeyValidator
        @Provides get() = this

    val UpdateMatchReportInteractor.bind: UpdateMatchReports
        @Provides get() = this

    val FixWrongPlayersInteractor.bind: FixWrongPlayers
        @Provides get() = this
}