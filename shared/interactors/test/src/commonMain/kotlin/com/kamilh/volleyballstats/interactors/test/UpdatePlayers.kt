package com.kamilh.volleyballstats.interactors.test

import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.interactors.UpdatePlayers
import com.kamilh.volleyballstats.interactors.UpdatePlayersParams
import com.kamilh.volleyballstats.interactors.UpdatePlayersResult
import com.kamilh.volleyballstats.utils.testAppDispatchers

fun updatePlayersOf(
    appDispatchers: AppDispatchers = testAppDispatchers,
    invoke: (params: UpdatePlayersParams) -> UpdatePlayersResult = { UpdatePlayersResult.success(Unit) },
): UpdatePlayers = object : UpdatePlayers(appDispatchers) {

    override suspend fun doWork(params: UpdatePlayersParams): UpdatePlayersResult = invoke(params)
}
