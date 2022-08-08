package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.interactor.Interactor
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.network.NetworkError
import com.kamilh.volleyballstats.network.repository.PolishLeagueRepository
import com.kamilh.volleyballstats.storage.InsertTeamError
import com.kamilh.volleyballstats.storage.TeamStorage
import me.tatarka.inject.annotations.Inject

typealias UpdateTeams = Interactor<UpdateTeamsParams, UpdateTeamsResult>

data class UpdateTeamsParams(val tour: Tour)

typealias UpdateTeamsResult = Result<Unit, UpdateTeamsError>

sealed class UpdateTeamsError(override val message: String? = null) : Error {
    class Network(val networkError: NetworkError) : UpdateTeamsError("Network(networkError: ${networkError.message})")
    class Storage(val insertTeamError: InsertTeamError) : UpdateTeamsError("Storage(insertTeamError: ${insertTeamError.message})")
}

@Inject
class UpdateTeamsInteractor(
    appDispatchers: AppDispatchers,
    private val polishLeagueRepository: PolishLeagueRepository,
    private val teamStorage: TeamStorage,
) : UpdateTeams(appDispatchers) {

    override suspend fun doWork(params: UpdateTeamsParams): UpdateTeamsResult =
        polishLeagueRepository.getTeams(params.tour)
            .mapError { UpdateTeamsError.Network(it) }
            .flatMap { updateTeams(it, params) }

    private suspend fun updateTeams(players: List<Team>, params: UpdateTeamsParams): UpdateTeamsResult =
        teamStorage.insert(players, params.tour.id)
            .mapError { UpdateTeamsError.Storage(it) }
}
