package com.kamilh.volleyballstats.clients.app.ui.navigation.node

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionSpec
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.*
import kotlin.math.roundToInt

@Suppress("TransitionPropertiesLabel")
private class BackStackSlider<T> : ModifierTransitionHandler<T, BackStack.State>() {

    private val offsetTransitionSpec: TransitionSpec<BackStack.State, Offset> = {
        tween(durationMillis = DURATION_ANIMATION_MS, easing = EASING)
    }

    private val floatTransitionSpec: TransitionSpec<BackStack.State, Float> = {
        tween(durationMillis = DURATION_ANIMATION_MS, easing = EASING)
    }

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<BackStack.State>,
        descriptor: TransitionDescriptor<T, BackStack.State>
    ): Modifier = modifier.composed {
        val offset by transition.animateOffset(
            transitionSpec = offsetTransitionSpec,
            targetValueByState = {
                val width = descriptor.params.bounds.width.value
                when (it) {
                    BackStack.State.CREATED -> toOutsideRight(width)
                    BackStack.State.ACTIVE -> toCenter()
                    BackStack.State.STASHED -> toOutsideLeft(width)
                    BackStack.State.DESTROYED -> {
                        when (val operation = descriptor.operation as? BackStackOperation) {
                            is Push, is Pop, is Remove, is SingleTop.SingleTopReactivateBackStackOperation ->
                                toOutsideRight(width)
                            is Replace, is NewRoot, is SingleTop.SingleTopReplaceBackStackOperation ->
                                toOutsideLeft(width)
                            null -> error("Unexpected operation: $operation")
                            else -> toOutsideRight(width)
                        }
                    }
                }
            }
        )
        val alpha = transition.animateFloat(
            transitionSpec = floatTransitionSpec,
            targetValueByState = {
                when (it) {
                    BackStack.State.CREATED -> 1.0f
                    BackStack.State.ACTIVE -> 1.0f
                    BackStack.State.STASHED -> 0.4f
                    BackStack.State.DESTROYED -> 1.0f
                }
            }
        )

        offset {
            offset.toIntOffset(density)
        }.graphicsLayer(
            alpha = alpha.value
        )
    }

    private fun toOutsideRight(width: Float): Offset =
        Offset(1.0f * width, 0f)

    private fun toOutsideLeft(width: Float): Offset =
        Offset(-.2f * width, 0f)

    private fun toCenter(): Offset =
        Offset(0f, 0f)

    companion object {
        private const val DURATION_ANIMATION_MS = 400
        private val EASING = FastOutSlowInEasing
    }
}

private fun Offset.toIntOffset(density: Float) = IntOffset(
    x = (x * density).roundToInt(),
    y = (y * density).roundToInt()
)

@Composable
fun <T> rememberBackstackSlider(): ModifierTransitionHandler<T, BackStack.State> = remember {
    BackStackSlider()
}
