package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.utils.testAppDispatchers

fun updatePlayersOf(
    appDispatchers: AppDispatchers = testAppDispatchers,
    invoke: (params: UpdatePlayersParams) -> UpdatePlayersResult = { UpdatePlayersResult.success(Unit) },
): UpdatePlayers = object : UpdatePlayers(appDispatchers) {

    override suspend fun doWork(params: UpdatePlayersParams): UpdatePlayersResult = invoke(params)
}
