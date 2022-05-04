package com.kamilh.routes

import com.kamilh.AppModule
import com.kamilh.authorization.CredentialsValidator
import com.kamilh.routes.matches.MatchesController
import com.kamilh.routes.players.PlayersController
import com.kamilh.routes.teams.TeamsController
import com.kamilh.routes.tours.ToursController
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

interface RoutesModule {

    val credentialsValidator: CredentialsValidator
        @Provides get

    val matchesController: MatchesController
        @Provides get

    val playersController: PlayersController
        @Provides get

    val teamsController: TeamsController
        @Provides get

    val toursController: ToursController
        @Provides get
}

@Component
abstract class AppRoutesModule(appModule: AppModule) : RoutesModule {

    override val credentialsValidator: CredentialsValidator = appModule.credentialsValidator

    override val matchesController: MatchesController = appModule.matchesController

    override val playersController: PlayersController = appModule.playersController

    override val teamsController: TeamsController = appModule.teamsController

    override val toursController: ToursController = appModule.toursController
}