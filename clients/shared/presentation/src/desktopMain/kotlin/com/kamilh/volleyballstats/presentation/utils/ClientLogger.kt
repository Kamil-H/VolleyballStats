package com.kamilh.volleyballstats.presentation.utils

import com.kamilh.volleyballstats.domain.utils.PlatformLogger
import com.kamilh.volleyballstats.domain.utils.Severity
import me.tatarka.inject.annotations.Inject

@Inject
actual class ClientLogger : PlatformLogger {

    override fun log(severity: Severity, tag: String?, message: String) {
        println("${severity.shorthand}/$tag: $message")
    }
}
