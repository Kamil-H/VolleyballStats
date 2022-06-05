package com.kamilh.volleyballstats.domain

import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

fun appDispatchersOf(
    io: CoroutineDispatcher = Dispatchers.Unconfined,
    main: CoroutineDispatcher = Dispatchers.Unconfined,
    default: CoroutineDispatcher = Dispatchers.Unconfined,
): AppDispatchers = AppDispatchers(
    io = io,
    main = main,
    default = default,
)