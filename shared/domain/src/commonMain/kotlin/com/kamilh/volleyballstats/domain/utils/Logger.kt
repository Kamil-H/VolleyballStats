package com.kamilh.volleyballstats.domain.utils

object Logger {

    const val TAG = "VolleyballStats"

    private var logger: PlatformLogger? = null

    fun setLogger(logger: PlatformLogger) {
        Logger.logger = logger
    }

    fun v(message: String, tag: String = TAG) {
        logger?.log(Severity.Verbose, tag, message)
    }

    fun d(message: String, tag: String = TAG) {
        logger?.log(Severity.Debug, tag, message)
    }

    fun i(message: String, tag: String = TAG) {
        logger?.log(Severity.Info, tag, message)
    }

    fun w(message: String, tag: String = TAG) {
        logger?.log(Severity.Warn, tag, message)
    }

    fun e(message: String, tag: String = TAG) {
        logger?.log(Severity.Error, tag, message)
    }
}