package com.kamilh

import com.kamilh.authorization.CredentialsValidator
import com.kamilh.authorization.credentialsValidatorOf
import com.kamilh.models.api.match.MatchResponse
import com.kamilh.models.api.match_report.MatchReportResponse
import com.kamilh.models.api.player_with_details.PlayerWithDetailsResponse
import com.kamilh.models.api.team.TeamResponse
import com.kamilh.models.api.tour.TourResponse
import com.kamilh.models.api.user.UserResponse
import com.kamilh.routes.RoutesModule
import com.kamilh.routes.matches.MatchesController
import com.kamilh.routes.players.PlayersController
import com.kamilh.routes.teams.TeamsController
import com.kamilh.routes.tours.ToursController
import routes.CallError
import routes.CallResult

fun routesModuleOf(
    credentialsValidator: CredentialsValidator = credentialsValidatorOf(),
    matchesController: MatchesController = matchesControllerOf(),
    playersController: PlayersController = playersControllerOf(),
    teamsController: TeamsController = teamsControllerOf(),
    toursController: ToursController = toursControllerOf(),
): RoutesModule = object : RoutesModule {
    override val credentialsValidator: CredentialsValidator = credentialsValidator
    override val matchesController: MatchesController = matchesController
    override val playersController: PlayersController = playersController
    override val teamsController: TeamsController = teamsController
    override val toursController: ToursController = toursController
}

fun userResponseOf(
    id: Long = 0,
    subscriptionKey: String = "",
    deviceId: String = "",
    createDate: String = "",
): UserResponse
= UserResponse(
    id = id,
    subscriptionKey = subscriptionKey,
    deviceId = deviceId,
    createDate = createDate,
)

fun matchesControllerOf(): MatchesController = object : MatchesController {
    override suspend fun getMatches(tourId: String?): CallResult<List<MatchResponse>> =
        CallResult.failure(CallError.missingParameterInPath())

    override suspend fun getMatchReport(matchId: String?): CallResult<MatchReportResponse> =
        CallResult.failure(CallError.missingParameterInPath())
}

fun playersControllerOf(): PlayersController = object : PlayersController {
    override suspend fun getPlayersWithDetails(tourId: String?): CallResult<List<PlayerWithDetailsResponse>> =
        CallResult.failure(CallError.missingParameterInPath())
}

fun teamsControllerOf(): TeamsController = object : TeamsController {
    override suspend fun getTeams(tourId: String?): CallResult<List<TeamResponse>> =
        CallResult.failure(CallError.missingParameterInPath())
}

fun toursControllerOf(): ToursController = object : ToursController {
    override suspend fun getTours(): CallResult<List<TourResponse>> =
        CallResult.failure(CallError.missingParameterInPath())
}
