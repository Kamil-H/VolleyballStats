package com.kamilh.interactors

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

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
    bind<GetAllSeason>() with provider {
        GetAllSeasonInteractor(instance(), instance(), instance(), instance())
    }
    bind<UpdatePlayers>() with provider {
        UpdatePlayersInteractor(instance(), instance())
    }
}