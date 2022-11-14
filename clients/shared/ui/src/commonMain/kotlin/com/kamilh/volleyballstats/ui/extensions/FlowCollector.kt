package com.kamilh.volleyballstats.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun <T> FlowCollector(flow: Flow<T>, collector: (T) -> Unit) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(flow) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { event ->
                collector(event)
            }
        }
    }
}

fun <T> ComponentActivity.collectSafely(flow: Flow<T>, collector: (T) -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { event ->
                collector(event)
            }
        }
    }
}
