package com.kamilh.volleyballstats.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.kamilh.volleyballstats.presentation.features.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

@Composable
inline fun <reified T : Presenter, reified E : Any> PresentersModule.rememberPresenter(extras: E): T =
    rememberPresenter(presenterMap = presenterMap, extras = extras)

@Composable
inline fun <reified T : Presenter> PresentersModule.rememberPresenter(): T =
    rememberPresenter(presenterMap = presenterMap, extras = Unit)

@Composable
inline fun <reified T : Presenter, reified E : Any> rememberPresenter(
    presenterMap: PresenterMap,
    extras: E,
): T {
    val compositionScope = rememberCoroutineScope()
    val coroutineScope = remember(key1 = compositionScope) {
        CoroutineScope(
            compositionScope.coroutineContext + SupervisorJob(compositionScope.coroutineContext[Job])
        )
    }
    return remember(key1 = extras) {
        create(
            presenterMap = presenterMap,
            coroutineScope = coroutineScope,
            savableMap = savableMapOf(),
            extras = extras,
        )
    }
}
