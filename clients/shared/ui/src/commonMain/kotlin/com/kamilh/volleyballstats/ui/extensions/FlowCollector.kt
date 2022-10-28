package com.kamilh.volleyballstats.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

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
