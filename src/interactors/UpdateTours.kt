package com.kamilh.interactors

import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import com.kamilh.storage.InsertTourError
import com.kamilh.storage.LeagueStorage
import com.kamilh.storage.TourStorage

typealias UpdateTours = Interactor<UpdateToursParams, UpdateToursResult>

data class UpdateToursParams(val league: League)

typealias UpdateToursResult = Result<Unit, UpdateToursError>

sealed class UpdateToursError(override val message: String? = null) : Error {
    class Network(val networkError: NetworkError) : UpdateToursError()
}

class UpdateToursInteractor(
    appDispatchers: AppDispatchers,
    private val polishLeagueRepository: PolishLeagueRepository,
    private val tourStorage: TourStorage,
    private val leagueStorage: LeagueStorage,
) : UpdateTours(appDispatchers) {

    override suspend fun doWork(params: UpdateToursParams): UpdateToursResult =
        polishLeagueRepository.getAllTours()
            .onSuccess { tours -> tours.forEach { insertTour(it) } }
            .map { }
            .mapError { UpdateToursError.Network(it) }

    private suspend fun insertTour(tour: Tour) {
        tourStorage.insert(tour)
            .onFailure {
                when (it) {
                    InsertTourError.LeagueNotFound -> {
                        leagueStorage.insert(tour.league)
                        insertTour(tour)
                    }
                    InsertTourError.TourAlreadyExists -> { }
                }
            }
    }
}