package com.kamilh.utils

fun interface ExceptionLogger {
    fun log(exception: Exception)
}

class ConsoleExceptionLogger : ExceptionLogger {

    override fun log(exception: Exception) {
        exception.printStackTrace()
    }
}