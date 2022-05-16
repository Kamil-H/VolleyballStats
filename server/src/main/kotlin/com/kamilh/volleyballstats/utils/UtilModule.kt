package com.kamilh.volleyballstats.utils

import com.kamilh.volleyballstats.Singleton
import me.tatarka.inject.annotations.Provides
import org.slf4j.LoggerFactory

interface UtilModule {

    @Provides
    @Singleton
    fun logger(): org.slf4j.Logger = LoggerFactory.getLogger(Logger.TAG)

    val Log4JLogger.bind: PlatformLogger
        @Provides get() = this

    val ConsoleExceptionLogger.bind: ExceptionLogger
        @Provides get() = this
}