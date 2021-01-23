package com.kamilh.utils

import java.util.*

fun String.toUUID(): UUID? =
    try {
        UUID.fromString(this)
    } catch (exception: IllegalArgumentException) {
        null
    }

fun interface UuidCreator {
    fun create(): UUID
}

class JavaUtilUuidCreator : UuidCreator {
    override fun create(): UUID = UUID.randomUUID()
}

fun interface UuidValidator {
    fun isValid(uuid: String): Boolean
}

class JavaUtilUuidValidator : UuidValidator {
    override fun isValid(uuid: String): Boolean = uuid.toUUID() != null
}