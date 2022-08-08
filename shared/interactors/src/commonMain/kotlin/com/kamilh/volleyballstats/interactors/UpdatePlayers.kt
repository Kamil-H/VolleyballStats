package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.interactor.Interactor
import com.kamilh.volleyballstats.domain.models.Error
import com.kamilh.volleyballstats.domain.models.Result
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.network.NetworkError
import com.kamilh.volleyballstats.storage.InsertPlayerError

typealias UpdatePlayers = Interactor<UpdatePlayersParams, UpdatePlayersResult>

data class UpdatePlayersParams(val tour: Tour)

typealias UpdatePlayersResult = Result<Unit, UpdatePlayersError>

sealed class UpdatePlayersError(override val message: String) : Error {
    class Network(val networkError: NetworkError) : UpdatePlayersError("Network(networkError: ${networkError.message})")
    class Storage(val insertPlayerError: InsertPlayerError) : UpdatePlayersError("Storage(insertPlayerError: ${insertPlayerError.message})")
}
