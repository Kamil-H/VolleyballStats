package com.kamilh.volleyballstats.ui.extensions

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow

@Composable
expect fun <T> FlowCollector(flow: Flow<T>, collector: (T) -> Unit)
