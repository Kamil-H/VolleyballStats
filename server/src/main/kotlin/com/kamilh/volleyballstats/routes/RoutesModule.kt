package com.kamilh.volleyballstats.routes

import com.kamilh.volleyballstats.routes.matches.MatchesController
import com.kamilh.volleyballstats.routes.matches.MatchesControllerImpl
import com.kamilh.volleyballstats.routes.players.PlayersController
import com.kamilh.volleyballstats.routes.players.PlayersControllerImpl
import com.kamilh.volleyballstats.routes.teams.TeamsController
import com.kamilh.volleyballstats.routes.teams.TeamsControllerImpl
import com.kamilh.volleyballstats.routes.tours.ToursController
import com.kamilh.volleyballstats.routes.tours.ToursControllerImpl
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
