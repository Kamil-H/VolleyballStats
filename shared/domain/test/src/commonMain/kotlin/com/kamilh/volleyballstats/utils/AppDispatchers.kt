package com.kamilh.volleyballstats.utils

import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import kotlinx.coroutines.Dispatchers

val testAppDispatchers = AppDispatchers(Dispatchers.Unconfined, Dispatchers.Unconfined, Dispatchers.Unconfined)