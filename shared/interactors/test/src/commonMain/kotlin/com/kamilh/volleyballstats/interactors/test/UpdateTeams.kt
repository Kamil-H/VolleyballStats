package com.kamilh.volleyballstats.interactors.test

import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.interactors.UpdateTeams
import com.kamilh.volleyballstats.interactors.UpdateTeamsParams
import com.kamilh.volleyballstats.interactors.UpdateTeamsResult
import com.kamilh.volleyballstats.utils.testAppDispatchers

fun updateTeamsOf(
    appDispatchers: AppDispatchers = testAppDispatchers,
    invoke: (params: UpdateTeamsParams) -> UpdateTeamsResult = { UpdateTeamsResult.success(Unit) },
): UpdateTeams = object : UpdateTeams(appDispatchers) {

    override suspend fun doWork(params: UpdateTeamsParams): UpdateTeamsResult = invoke(params)
}