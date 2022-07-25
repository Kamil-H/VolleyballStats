package com.kamilh.volleyballstats.routes

inline fun <T> String?.retrieveLongId(queryParamName: String? = null, idCreator: (Long) -> T): CallResult<T> {
    this ?: return CallResult.failure(
        if (queryParamName != null) {
            CallError.missingParameter(queryParam = queryParamName)
        } else {
            CallError.missingParameterInPath()
        }
    )
    val number = this.toLongOrNull() ?: return CallResult.failure(
        CallError.wrongParameterType(param = this, correctType = CORRECT_QUERY_PARAM_TYPE)
    )
    return CallResult.success(idCreator(number))
}

const val CORRECT_QUERY_PARAM_TYPE = "Integer"
