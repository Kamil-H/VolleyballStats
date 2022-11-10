package com.kamilh.volleyballstats.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.kamilh.volleyballstats.presentation.features.common.Icon

@Composable
expect fun Icon.toPainter(): Painter
