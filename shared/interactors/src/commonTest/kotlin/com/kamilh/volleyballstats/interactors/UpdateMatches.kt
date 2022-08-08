package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.utils.testAppDispatchers

fun updateMatchesOf(
    appDispatchers: AppDispatchers = testAppDispatchers,
    invoke: (params: UpdateMatchesParams) -> UpdateMatchesResult = { UpdateMatchesResult.success(UpdateMatchesSuccess.NothingToSchedule) },
): UpdateMatches = object : UpdateMatches(appDispatchers) {

    override suspend fun doWork(params: UpdateMatchesParams): UpdateMatchesResult = invoke(params)
}