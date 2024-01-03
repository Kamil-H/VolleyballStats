package com.kamilh.volleyballstats.presentation.utils

import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class Scope internal constructor(appDispatchers: AppDispatchers) {

    internal val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + appDispatchers.main)

    fun cancel() {
        coroutineScope.cancel()
    }
}
