package com.kamilh.routes

import com.kamilh.routes.matches.MatchesController
import com.kamilh.routes.matches.MatchesControllerImpl
import com.kamilh.routes.players.PlayersController
import com.kamilh.routes.players.PlayersControllerImpl
import com.kamilh.routes.teams.TeamsController
import com.kamilh.routes.teams.TeamsControllerImpl
import com.kamilh.routes.tours.ToursController
import com.kamilh.routes.tours.ToursControllerImpl
import me.tatarka.inject.annotations.Provides

interface RoutesModule {

    val MatchesControllerImpl.bind: MatchesController
        @Provides get() = this

    val PlayersControllerImpl.bind: PlayersController
        @Provides get() = this

    val TeamsControllerImpl.bind: TeamsController
        @Provides get() = this

    val ToursControllerImpl.bind: ToursController
        @Provides get() = this
}