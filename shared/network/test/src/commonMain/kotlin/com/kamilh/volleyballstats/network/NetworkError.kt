package com.kamilh.volleyballstats.repository.polishleague

import com.kamilh.volleyballstats.network.NetworkError

fun networkErrorOf(throwable: Throwable = Throwable()): NetworkError = NetworkError.createFrom(throwable)