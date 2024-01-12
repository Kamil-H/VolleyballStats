package com.kamilh.volleyballstats.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.kamilh.volleyballstats.ui.Resources
import com.kamilh.volleyballstats.presentation.features.common.Icon
import io.github.skeptick.libres.compose.painterResource

@Composable
fun Icon.toPainter(): Painter =
    when (this) {
        Icon.Scoreboard -> Resources.image.scoreboard_24
        Icon.Tune -> Resources.image.tune_24
        Icon.Person -> Resources.image.person_24
        Icon.Groups -> Resources.image.groups_24
        Icon.Refresh -> Resources.image.refresh_24
        Icon.ArrowBack -> Resources.image.arrow_back_24
    }.let { painterResource(image = it) }
