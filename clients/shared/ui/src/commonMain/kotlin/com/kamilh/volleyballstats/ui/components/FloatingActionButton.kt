package com.kamilh.volleyballstats.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * FAB with show/hide animations according to the Material spec
 * https://www.jetpackcompose.app/snippets/FABAnimations
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
@Suppress("MagicNumber")
fun FloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 15,
                delayMillis = 30,
                easing = LinearEasing,
            ),
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = 15,
                delayMillis = 150,
                easing = LinearEasing,
            )
        ),
    ) {
        val fabScale by transition.animateFloat(
            transitionSpec = {
                tween(
                    durationMillis = when (targetState) {
                        EnterExitState.PreEnter, EnterExitState.Visible -> 330
                        EnterExitState.PostExit -> 135
                    },
                    delayMillis = 0,
                    easing = LinearOutSlowInEasing,
                )
            },
            label = "FAB scale"
        ) {
            when (it) {
                EnterExitState.PreEnter, EnterExitState.PostExit -> 0f
                EnterExitState.Visible -> 1f
            }
        }
        FloatingActionButton(
            onClick = onClick,
            fabScale = fabScale,
            content = content,
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Suppress("MagicNumber")
private fun AnimatedVisibilityScope.FloatingActionButton(
    onClick: () -> Unit,
    fabScale: Float,
    content: @Composable () -> Unit,
) {
    androidx.compose.material3.FloatingActionButton(
        modifier = Modifier.graphicsLayer {
            scaleX = fabScale
            scaleY = fabScale
        },
        onClick = onClick,
    ) {
        val contentScale by transition.animateFloat(
            transitionSpec = {
                tween(
                    durationMillis = when (targetState) {
                        EnterExitState.PreEnter, EnterExitState.Visible -> 240
                        EnterExitState.PostExit -> 135
                    },
                    delayMillis = when (targetState) {
                        EnterExitState.PreEnter, EnterExitState.Visible -> 90
                        EnterExitState.PostExit -> 0
                    },
                    easing = FastOutLinearInEasing,
                )
            },
            label = "FAB content scale"
        ) {
            when (it) {
                EnterExitState.PreEnter, EnterExitState.PostExit -> 0f
                EnterExitState.Visible -> 1f
            }
        }
        Box(
            Modifier.graphicsLayer {
                scaleX = contentScale
                scaleY = contentScale
            }
        ) {
            content()
        }
    }
}
