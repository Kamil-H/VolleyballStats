package com.kamilh.volleyballstats.domain.utils

import me.tatarka.inject.annotations.Inject

fun interface ExceptionLogger {
    fun log(exception: Exception)
}

@Inject
class ConsoleExceptionLogger : ExceptionLogger {

    override fun log(exception: Exception) {
        Logger.e(message = exception.stackTraceToString())
    }
}
