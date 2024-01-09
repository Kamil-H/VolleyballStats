package com.kamilh.volleyballstats.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.bumble.appyx.navigation.node.Node
import com.kamilh.volleyballstats.presentation.features.Presenter
import com.kamilh.volleyballstats.presentation.features.PresenterMap
import com.kamilh.volleyballstats.presentation.features.SavableMap
import com.kamilh.volleyballstats.presentation.features.create
import com.kamilh.volleyballstats.presentation.features.savableMapOf
import com.kamilh.volleyballstats.presentation.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

@Composable
inline fun <reified T : Presenter, reified E : Screen> PresenterMap.rememberPresenter(screen: E): T {
    val compositionScope = rememberCoroutineScope()
    val coroutineScope = remember(key1 = compositionScope) {
        CoroutineScope(
            compositionScope.coroutineContext + SupervisorJob(compositionScope.coroutineContext[Job])
        )
    }
    return remember(key1 = screen) {
        create(
            presenterMap = this,
            coroutineScope = coroutineScope,
            savableMap = savableMapOf(),
            screen = screen,
        )
    }
}

internal inline fun <reified T : Presenter, reified E : Screen> Node.presenter(
    presenterMap: PresenterMap,
    savableMap: SavableMap = savableMapOf(),
    screen: E,
): T {
    val compositionScope = lifecycleScope
    val coroutineScope = CoroutineScope(
        compositionScope.coroutineContext + SupervisorJob(compositionScope.coroutineContext[Job])
    )
    return create(
        presenterMap = presenterMap,
        coroutineScope = coroutineScope,
        savableMap = savableMap,
        screen = screen,
    )
}

