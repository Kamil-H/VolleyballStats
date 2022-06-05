package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.network.NetworkError
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.repository.polishleague.PolishLeagueRepository
import com.kamilh.volleyballstats.storage.InsertTourError
import com.kamilh.volleyballstats.storage.LeagueStorage
import com.kamilh.volleyballstats.storage.TourStorage
import me.tatarka.inject.annotations.Inject

typealias UpdateTours = Interactor<UpdateToursParams, UpdateToursResult>

data class UpdateToursParams(val league: League)

typealias UpdateToursResult = Result<Unit, UpdateToursError>

sealed class UpdateToursError(override val message: String) : Error {
    class Network(val networkError: NetworkError) : UpdateToursError("Network(networkError: ${networkError.message})")
}

@Inject
class UpdateToursInteractor(
    appDispatchers: AppDispatchers,
    private val polishLeagueRepository: PolishLeagueRepository,
    private val tourStorage: TourStorage,
    private val leagueStorage: LeagueStorage,
) : UpdateTours(appDispatchers) {

    override suspend fun doWork(params: UpdateToursParams): UpdateToursResult =
        polishLeagueRepository.getAllTours()
            .onSuccess { tours -> tours.forEach { insertTour(it, shouldTryInsertLeagueOnError = true) } }
            .map { }
            .mapError { UpdateToursError.Network(it) }

    private suspend fun insertTour(tour: Tour, shouldTryInsertLeagueOnError: Boolean) {
        tourStorage.insert(tour)
            .onFailure {
                when (it) {
                    InsertTourError.LeagueNotFound -> {
                        if (shouldTryInsertLeagueOnError) {
                            leagueStorage.insert(tour.league)
                            insertTour(tour, shouldTryInsertLeagueOnError = false)
                        }
                    }
                    InsertTourError.TourAlreadyExists -> { }
                }
            }
    }
}