package com.kamilh.utils

import me.tatarka.inject.annotations.Inject

fun interface ExceptionLogger {
    fun log(exception: Exception)
}

@Inject
class ConsoleExceptionLogger : ExceptionLogger {

    override fun log(exception: Exception) {
        exception.printStackTrace()
    }
}