package com.kamilh.volleyballstats.clients.app.ui.extensions

import androidx.compose.ui.Modifier

inline fun Modifier.conditional(condition : Boolean, modifier : Modifier.() -> Modifier) : Modifier =
    if (condition) then(modifier(this)) else this
