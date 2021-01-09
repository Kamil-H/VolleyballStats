package com.kamilh.utils

import java.util.*

fun String.toUUID(): UUID? =
    try {
        UUID.fromString(this)
    } catch (exception: IllegalArgumentException) {
        null
    }