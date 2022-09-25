package com.kamilh.volleyballstats.clients.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.kamilh.volleyballstats.clients.app.R

@Composable
private fun DarkColorPalette(): Colors = darkColors(
    primary = colorResource(R.color.orange),
    primaryVariant = Purple700,
    secondary = colorResource(R.color.turquoise)
)

@Composable
private fun LightColorPalette(): Colors = lightColors(
    primary = colorResource(R.color.orange),
    primaryVariant = Purple700,
    secondary = colorResource(R.color.turquoise)

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun EmptyComposeTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette()
    } else {
        LightColorPalette()
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}