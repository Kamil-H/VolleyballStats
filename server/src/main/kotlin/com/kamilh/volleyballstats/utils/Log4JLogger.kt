package com.kamilh.volleyballstats.utils

import com.kamilh.volleyballstats.domain.utils.PlatformLogger
import com.kamilh.volleyballstats.domain.utils.Severity
import me.tatarka.inject.annotations.Inject

@Inject
class Log4JLogger(private val logger: org.slf4j.Logger) : PlatformLogger {

    override fun log(severity: Severity, tag: String, message: String) {
        when (severity) {
            Severity.Verbose, Severity.Info -> logger.info(message)
            Severity.Debug -> logger.debug(message)
            Severity.Warn -> logger.warn(message)
            Severity.Error -> logger.error(message)
        }
    }
}