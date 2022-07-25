package com.kamilh.volleyballstats.network

import com.kamilh.volleyballstats.domain.models.Result

typealias NetworkResult<T> = Result<T, NetworkError>

typealias UnitNetworkResult = NetworkResult<Unit>
