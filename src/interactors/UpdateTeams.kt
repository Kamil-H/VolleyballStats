package com.kamilh.interactors

import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import com.kamilh.storage.InsertTeamError
import com.kamilh.storage.TeamStorage

typealias UpdateTeams = Interactor<UpdateTeamsParams, UpdateTeamsResult>

data class UpdateTeamsParams(val league: League, val tour: TourYear)

typealias UpdateTeamsResult = Result<Unit, UpdateTeamsError>

sealed class UpdateTeamsError(override val message: String? = null) : Error {
    class Network(val networkError: NetworkError) : UpdateTeamsError("Network(networkError: ${networkError.message})")
    class Storage(val insertTeamError: InsertTeamError) : UpdateTeamsError("Storage(insertTeamError: ${insertTeamError.message})")
}

class UpdateTeamsInteractor(
    appDispatchers: AppDispatchers,
    private val polishLeagueRepository: PolishLeagueRepository,
    private val teamStorage: TeamStorage,
) : UpdateTeams(appDispatchers) {

    override suspend fun doWork(params: UpdateTeamsParams): UpdateTeamsResult =
        polishLeagueRepository.getAllTeams(params.tour)
            .mapError { UpdateTeamsError.Network(it) }
            .flatMap { updateTeams(it, params) }

    private suspend fun updateTeams(players: List<Team>, params: UpdateTeamsParams): UpdateTeamsResult =
        teamStorage.insert(players, params.league, params.tour)
            .mapError { UpdateTeamsError.Storage(it) }
}