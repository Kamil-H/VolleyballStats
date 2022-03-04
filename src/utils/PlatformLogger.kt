package utils

fun interface PlatformLogger {

    fun log(severity: Severity, tag: String, message: String)
}

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

object Logger {

    const val TAG = "VolleyballStats"
    
    private var logger: PlatformLogger? = null

    fun setLogger(logger: PlatformLogger) {
        this.logger = logger
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

enum class Severity(val shorthand: String) {
    Verbose(shorthand = "v"),
    Debug(shorthand = "d"),
    Info(shorthand = "i"),
    Warn(shorthand = "w"),
    Error(shorthand = "e"),
}