package com.kamilh.volleyballstats.ui.navigation.node

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.kamilh.volleyballstats.presentation.features.PresenterMap
import com.kamilh.volleyballstats.presentation.navigation.Screen
import com.kamilh.volleyballstats.ui.screens.filters.PlayerFiltersNode
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class TabNode(
    buildContext: BuildContext,
    private val backStack: BackStack<Screen.Full>,
    private val presenterMap: PresenterMap,
    private val rootNode: (buildContext: BuildContext) -> Node,
) : ParentNode<Screen.Full>(appyxComponent = backStack, buildContext = buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        AppyxComponent(
            modifier = modifier.fillMaxSize(),
            appyxComponent = backStack,
        )
    }

    override fun resolve(interactionTarget: Screen.Full, buildContext: BuildContext): Node =
        when (interactionTarget) {
            is Screen.Filters -> PlayerFiltersNode(
                buildContext = buildContext,
                presenterMap = presenterMap,
                filters = interactionTarget,
            )
            Screen.Full.Root -> rootNode(buildContext)
        }
}
