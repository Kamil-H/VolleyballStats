package com.kamilh.volleyballstats.models

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

fun appDispatchersOf(
    io: CoroutineDispatcher = Dispatchers.Unconfined,
    main: CoroutineDispatcher = Dispatchers.Unconfined,
    default: CoroutineDispatcher = Dispatchers.Unconfined,
) : AppDispatchers =
    AppDispatchers(
        io = io,
        main = main,
        default = default,
    )