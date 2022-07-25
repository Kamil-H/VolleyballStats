package com.kamilh.volleyballstats.utils

import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.utils.ConsoleExceptionLogger
import com.kamilh.volleyballstats.domain.utils.ExceptionLogger
import com.kamilh.volleyballstats.domain.utils.Logger
import com.kamilh.volleyballstats.domain.utils.PlatformLogger
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
