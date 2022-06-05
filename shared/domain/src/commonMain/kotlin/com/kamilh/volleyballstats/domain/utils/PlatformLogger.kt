package com.kamilh.volleyballstats.domain.utils

fun interface PlatformLogger {

    fun log(severity: Severity, tag: String, message: String)
}

enum class Severity(val shorthand: String) {
    Verbose(shorthand = "v"),
    Debug(shorthand = "d"),
    Info(shorthand = "i"),
    Warn(shorthand = "w"),
    Error(shorthand = "e"),
}