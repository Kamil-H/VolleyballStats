@file:Suppress("TopLevelPropertyNaming")

package com.kamilh.volleyballstats.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = AppColor.LightPrimary,
    onPrimary = AppColor.LightOnPrimary,
    primaryContainer = AppColor.LightPrimaryContainer,
    onPrimaryContainer = AppColor.LightOnPrimaryContainer,
    secondary = AppColor.LightSecondary,
    onSecondary = AppColor.LightOnSecondary,
    secondaryContainer = AppColor.LightSecondaryContainer,
    onSecondaryContainer = AppColor.LightOnSecondaryContainer,
    tertiary = AppColor.LightTertiary,
    onTertiary = AppColor.LightOnTertiary,
    tertiaryContainer = AppColor.LightTertiaryContainer,
    onTertiaryContainer = AppColor.LightOnTertiaryContainer,
    error = AppColor.LightError,
    errorContainer = AppColor.LightErrorContainer,
    onError = AppColor.LightOnError,
    onErrorContainer = AppColor.LightOnErrorContainer,
    background = AppColor.LightBackground,
    onBackground = AppColor.LightOnBackground,
    surface = AppColor.LightSurface,
    onSurface = AppColor.LightOnSurface,
    surfaceVariant = AppColor.LightSurfaceVariant,
    onSurfaceVariant = AppColor.LightOnSurfaceVariant,
    outline = AppColor.LightOutline,
    inverseOnSurface = AppColor.LightInverseOnSurface,
    inverseSurface = AppColor.LightInverseSurface,
    inversePrimary = AppColor.LightInversePrimary,
    surfaceTint = AppColor.LightSurfaceTint,
)


private val DarkColors = darkColorScheme(
    primary = AppColor.DarkPrimary,
    onPrimary = AppColor.DarkOnPrimary,
    primaryContainer = AppColor.DarkPrimaryContainer,
    onPrimaryContainer = AppColor.DarkOnPrimaryContainer,
    secondary = AppColor.DarkSecondary,
    onSecondary = AppColor.DarkOnSecondary,
    secondaryContainer = AppColor.DarkSecondaryContainer,
    onSecondaryContainer = AppColor.DarkOnSecondaryContainer,
    tertiary = AppColor.DarkTertiary,
    onTertiary = AppColor.DarkOnTertiary,
    tertiaryContainer = AppColor.DarkTertiaryContainer,
    onTertiaryContainer = AppColor.DarkOnTertiaryContainer,
    error = AppColor.DarkError,
    errorContainer = AppColor.DarkErrorContainer,
    onError = AppColor.DarkOnError,
    onErrorContainer = AppColor.DarkOnErrorContainer,
    background = AppColor.GreyDark,
    onBackground = AppColor.White,
    surface = AppColor.Grey,
    onSurface = AppColor.White,
    surfaceVariant = AppColor.DarkSurfaceVariant,
    onSurfaceVariant = AppColor.DarkOnSurfaceVariant,
    outline = AppColor.DarkOutline,
    inverseOnSurface = AppColor.DarkInverseOnSurface,
    inverseSurface = AppColor.DarkInverseSurface,
    inversePrimary = AppColor.DarkInversePrimary,
    surfaceTint = AppColor.DarkSurfaceTint,
)

@Composable
fun VolleyballStatsTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColors else LightColors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
