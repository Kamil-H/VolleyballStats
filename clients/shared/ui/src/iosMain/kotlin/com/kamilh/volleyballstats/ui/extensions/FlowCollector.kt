package com.kamilh.volleyballstats.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
actual fun <T> FlowCollector(flow: Flow<T>, collector: (T) -> Unit) {
    LaunchedEffect(flow) {
        flow.collect { event ->
            collector(event)
        }
    }
}
