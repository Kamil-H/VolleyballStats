package com.kamilh.volleyballstats.utils

import com.kamilh.volleyballstats.domain.utils.PlatformLogger
import com.kamilh.volleyballstats.domain.utils.Severity
import me.tatarka.inject.annotations.Inject

@Inject
class Log4JLogger(private val logger: org.slf4j.Logger) : PlatformLogger {

    override fun log(severity: Severity, tag: String?, message: String) {
        val logMessage = tag?.let { "$tag: $message" } ?: message
        when (severity) {
            Severity.Verbose, Severity.Info -> logger.info(logMessage)
            Severity.Debug -> logger.debug(logMessage)
            Severity.Warn -> logger.warn(logMessage)
            Severity.Error -> logger.error(logMessage)
        }
    }
}