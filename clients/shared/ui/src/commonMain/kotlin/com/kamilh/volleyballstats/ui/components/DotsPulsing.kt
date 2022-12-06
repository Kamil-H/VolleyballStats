package com.kamilh.volleyballstats.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PulsingDots(
    modifier: Modifier = Modifier,
    numberOfDots: Int = 3,
    dotSize: Dp = 32.dp,
    dotColor: Color = MaterialTheme.colorScheme.primary,
    delayUnit: Int = 400,
    spaceBetween: Dp = 10.dp,
) {
    val scales = arrayListOf<State<Float>>()
    for (i in 0 until numberOfDots) {
        scales.add(
            animateScaleWithDelay(
                delay = i * delayUnit,
                numberOfDots = numberOfDots,
                delayUnit = delayUnit,
                duration = numberOfDots * delayUnit,
            )
        )
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        scales.forEach {
            Dot(
                scale = it.value,
                dotSize = dotSize,
                dotColor = dotColor,
            )
            Spacer(Modifier.width(spaceBetween))
        }
    }
}

@Composable
private fun Dot(
    scale: Float,
    modifier: Modifier = Modifier,
    dotSize: Dp = 10.dp,
    dotColor: Color = Color.Blue,
) {
    Spacer(
        modifier.size(dotSize).scale(scale).background(
            color = dotColor, shape = CircleShape
        )
    )
}

@Composable
private fun animateScaleWithDelay(
    delay: Int,
    numberOfDots: Int,
    delayUnit: Int,
    duration: Int,
): State<Float> = rememberInfiniteTransition().animateFloat(
    initialValue = 0f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
        animation = keyframes {
            durationMillis = delayUnit * numberOfDots
            0f at delay with LinearEasing
            1f at delay + delayUnit with LinearEasing
            0f at delay + duration
        }
    )
)

