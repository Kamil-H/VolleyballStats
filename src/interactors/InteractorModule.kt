package com.kamilh.interactors

import org.kodein.di.*
import utils.Logger

private const val MODULE_NAME = "DI_INTERACTOR_MODULE"
val interactorModule = DI.Module(name = MODULE_NAME) {
    bind<AddUser>() with provider {
        AddUserInteractor(instance(), instance(), instance())
    }
    bind<GetUser>() with provider {
        GetUserInteractor(instance(), instance())
    }
    bind<SubscriptionKeyValidator>() with provider {
        SubscriptionKeyValidatorInteractor(instance(), instance())
    }
    bind<UpdatePlayers>() with provider {
        UpdatePlayersInteractor(instance(), instance(), instance())
    }
    bind<UpdateMatches>() with provider {
        UpdateMatchesInteractor(instance(), instance(), instance(), instance(), instance())
    }
    bind<UpdateTeams>() with provider {
        UpdateTeamsInteractor(instance(), instance(), instance())
    }
    bind<UpdateMatchReports>() with provider {
        UpdateMatchReportInteractor(instance(), instance(), instance())
    }
    bind<UpdateTours>() with provider {
        UpdateToursInteractor(instance(), instance(), instance(), instance())
    }
    bind<FixWrongPlayers>() with provider {
        FixWrongPlayersInteractor(instance(), instance(), instance())
    }
    bind<MatchReportPreparer>() with provider {
        MatchReportPreparerInteractor(instance(), instance(), instance(), instance())
    }
    bind<SynchronizeScheduler>() with provider {
        SynchronizeScheduler {
            Logger.i("Scheduling... $it")
        }
    }
    bindProvider {
        Synchronizer(instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance())
    }
}