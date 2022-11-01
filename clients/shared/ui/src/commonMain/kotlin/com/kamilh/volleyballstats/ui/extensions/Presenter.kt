package com.kamilh.volleyballstats.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.kamilh.volleyballstats.presentation.features.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

@Composable
inline fun <reified T : Presenter, reified E : Any> PresentersModule.presenter(extras: E): T =
    presenter(presenterMap = presenterMap, extras = extras)

@Composable
inline fun <reified T : Presenter> PresentersModule.presenter(): T =
    presenter(presenterMap = presenterMap, extras = Unit)

@Composable
inline fun <reified T : Presenter, reified E : Any> presenter(
    presenterMap: PresenterMap,
    extras: E,
): T {
    val compositionScope = rememberCoroutineScope()
    val coroutineScope = CoroutineScope(
        compositionScope.coroutineContext + SupervisorJob(compositionScope.coroutineContext[Job])
    )
    return create(
        presenterMap = presenterMap,
        coroutineScope = coroutineScope,
        savableMap = savableMapOf(),
        extras = extras,
    )
}
