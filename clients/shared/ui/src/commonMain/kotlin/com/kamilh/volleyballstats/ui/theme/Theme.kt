package com.kamilh.volleyballstats.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.kamilh.volleyballstats.presentation.features.ColorAccent

@Composable
fun VolleyballStatsTheme(
    colorAccent: ColorAccent,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = when (colorAccent) {
            ColorAccent.Primary -> if (useDarkTheme) OrangeDarkColors else OrangeLightColors
            ColorAccent.Tertiary -> if (useDarkTheme) TurquoiseDarkColors else TurquoiseLightColors
            ColorAccent.Default -> if (useDarkTheme) OrangeDarkColors else OrangeLightColors
        },
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
