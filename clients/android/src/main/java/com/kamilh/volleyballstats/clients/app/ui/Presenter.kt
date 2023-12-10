package com.kamilh.volleyballstats.clients.app.ui

import com.bumble.appyx.navigation.node.Node
import com.kamilh.volleyballstats.presentation.features.Presenter
import com.kamilh.volleyballstats.presentation.features.PresenterMap
import com.kamilh.volleyballstats.presentation.features.SavableMap
import com.kamilh.volleyballstats.presentation.features.create
import com.kamilh.volleyballstats.presentation.features.savableMapOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

inline fun <reified T : Presenter> Node.presenter(
    presenterMap: PresenterMap,
    savableMap: SavableMap = savableMapOf(),
): T = presenter(
    presenterMap = presenterMap,
    savableMap = savableMap,
    extras = Unit,
)

inline fun <reified T : Presenter, reified E : Any> Node.presenter(
    presenterMap: PresenterMap,
    savableMap: SavableMap = savableMapOf(),
    extras: E,
): T {
    val compositionScope = lifecycleScope
    val coroutineScope = CoroutineScope(
        compositionScope.coroutineContext + SupervisorJob(compositionScope.coroutineContext[Job])
    )
    return create(
        presenterMap = presenterMap,
        coroutineScope = coroutineScope,
        savableMap = savableMap,
        extras = extras,
    )
}
