package com.kamilh.volleyballstats.ui.extensions

import androidx.compose.ui.Modifier

inline fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier =
    if (condition) then(modifier(this)) else this

inline fun <T : Any> Modifier.ifNotNull(value: T?, modifier: Modifier.(T) -> Modifier): Modifier =
    value?.let { then(modifier(it)) } ?: this
