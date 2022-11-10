package com.kamilh.volleyballstats.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.kamilh.volleyballstats.presentation.features.common.Icon
import com.kamilh.volleyballstats.ui.R

@Composable
actual fun Icon.toPainter(): Painter =
    when (this) {
        Icon.Scoreboard -> R.drawable.scoreboard_24
        Icon.Tune -> R.drawable.tune_24
        Icon.Person -> R.drawable.person_24
        Icon.Groups -> R.drawable.groups_24
    }.let { painterResource(id = it) }
