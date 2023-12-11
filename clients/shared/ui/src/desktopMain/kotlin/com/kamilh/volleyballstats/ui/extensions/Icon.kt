package com.kamilh.volleyballstats.ui.extensions

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.kamilh.volleyballstats.presentation.features.common.Icon

@Composable
actual fun Icon.toPainter(): Painter = rememberVectorPainter(IconMenu)

@Suppress("TopLevelPropertyNaming", "MagicNumber")
private val IconMenu: ImageVector = materialIcon(name = "Filled.Menu") {
    materialPath {
        moveTo(3.0f, 18.0f)
        horizontalLineToRelative(18.0f)
        verticalLineToRelative(-2.0f)
        lineTo(3.0f, 16.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(3.0f, 13.0f)
        horizontalLineToRelative(18.0f)
        verticalLineToRelative(-2.0f)
        lineTo(3.0f, 11.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(3.0f, 6.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(18.0f)
        lineTo(21.0f, 6.0f)
        lineTo(3.0f, 6.0f)
        close()
    }
}
