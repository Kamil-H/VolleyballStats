package com.kamilh.volleyballstats.presentation.features.common

data class SegmentedControlState(
    val items: List<String>,
    val selectedIndex: Int = 0,
)
