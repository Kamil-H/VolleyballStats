package com.kamilh.volleyballstats.presentation.utils

import platform.Foundation.NSLog
import com.kamilh.volleyballstats.domain.utils.PlatformLogger
import com.kamilh.volleyballstats.domain.utils.Severity

actual class ClientLogger : PlatformLogger {

    override fun log(severity: Severity, tag: String?, message: String) {
        NSLog("${severity.shorthand}/$tag: $message")
    }
}