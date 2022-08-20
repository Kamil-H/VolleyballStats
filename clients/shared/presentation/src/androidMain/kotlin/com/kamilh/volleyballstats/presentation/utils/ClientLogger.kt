package com.kamilh.volleyballstats.presentation.utils

import android.util.Log
import com.kamilh.volleyballstats.domain.utils.PlatformLogger
import com.kamilh.volleyballstats.domain.utils.Severity
import me.tatarka.inject.annotations.Inject

@Inject
actual class ClientLogger : PlatformLogger {
    override fun log(severity: Severity, tag: String?, message: String) {
        when (severity) {
            Severity.Verbose -> Log.v(tag, message)
            Severity.Debug -> Log.d(tag, message)
            Severity.Info -> Log.i(tag, message)
            Severity.Warn -> Log.w(tag, message)
            Severity.Error -> Log.e(tag, message)
        }
    }
}
