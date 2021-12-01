package com.kamilh.utils

import com.kamilh.models.AppDispatchers
import kotlinx.coroutines.Dispatchers

val testAppDispatchers = AppDispatchers(Dispatchers.Unconfined, Dispatchers.Unconfined, Dispatchers.Unconfined)