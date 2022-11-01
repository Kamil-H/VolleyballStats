package com.kamilh.volleyballstats.presentation.features.common

typealias ChooseIntState = ChooseValueState<Int>

data class ChooseValueState<T: Number>(
    val title: String,
    val value: T,
    val maxValue: T,
    val onValueSelected: (T) -> Unit,
)

fun <T: Number> ChooseValueState<T>.setNewValue(value: T): ChooseValueState<T> = copy(value = value)
