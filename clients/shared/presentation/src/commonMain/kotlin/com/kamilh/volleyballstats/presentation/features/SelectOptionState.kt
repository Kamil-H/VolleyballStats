package com.kamilh.volleyballstats.presentation.features

data class SelectOptionState<T : Any>(
    val title: String? = null,
    val options: List<Option<T>>,
    val onSelected: (id: T) -> Unit,
) {

    val visible: Boolean = options.isNotEmpty()

    data class Option<T : Any>(
        val id: T,
        val label: String,
        val selected: Boolean,
    )
}

fun <T : Any> SelectOptionState<T>.select(id: T): SelectOptionState<T> =
    copy(
        options = options.map { option ->
            if (option.id == id) {
                option.copy(selected = !option.selected)
            } else {
                option
            }
        }
    )

fun <T : Any> SelectOptionState<T>.selectSingle(id: T): SelectOptionState<T> =
    copy(
        options = options.map { option ->
            if (option.id == id) {
                option.copy(selected = !option.selected)
            } else {
                option.copy(selected = false)
            }
        }
    )
