package com.kamilh.volleyballstats.clients.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kamilh.volleyballstats.clients.app.R

private val PoppinsFontFamily = FontFamily(
    Font(R.font.poppins_regular),
    Font(R.font.poppins_medium, FontWeight.W500),
    Font(R.font.poppins_bold, FontWeight.W700)
)

internal val Typography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 57.sp,
        fontFamily = PoppinsFontFamily,
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 45.sp,
        fontFamily = PoppinsFontFamily,
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 36.sp,
        fontFamily = PoppinsFontFamily,
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 32.sp,
        fontFamily = PoppinsFontFamily,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 28.sp,
        fontFamily = PoppinsFontFamily,
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 24.sp,
        fontFamily = PoppinsFontFamily,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.W700,
        fontSize = 22.sp,
        fontFamily = PoppinsFontFamily,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.W700,
        fontSize = 16.sp,
        fontFamily = PoppinsFontFamily,
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        fontFamily = PoppinsFontFamily,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        fontFamily = PoppinsFontFamily,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        fontFamily = PoppinsFontFamily,
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        fontFamily = PoppinsFontFamily,
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        fontFamily = PoppinsFontFamily,
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        fontFamily = PoppinsFontFamily,
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 10.sp,
        fontFamily = PoppinsFontFamily,
    )
)
