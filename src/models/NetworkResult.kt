package com.kamilh.models

typealias NetworkResult<T> = Result<T, NetworkError>

typealias UnitNetworkResult = NetworkResult<Unit>