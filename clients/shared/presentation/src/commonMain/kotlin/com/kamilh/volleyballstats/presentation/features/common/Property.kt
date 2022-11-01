package com.kamilh.volleyballstats.presentation.features.common

interface Property<T : Any> {
    val id: T

    val shortName: String

    val additionalName: String?

    val description: String

    val mandatory: Boolean

    val filterable: Boolean
}

val <T: Any> Property<T>.title: String
    get() = if (additionalName != null) {
        "$shortName $additionalName"
    } else {
        shortName
    }

data class CheckableProperty<T : Any>(
    val checked: Boolean,
    val property: Property<T>,
) : Property<T> by property

data class ChoosePropertiesState<T : Any>(
    val title: String? = null,
    val checkableProperties: List<CheckableProperty<T>>,
    val onChecked: (T) -> Unit,
) {
    val visible: Boolean = checkableProperties.isNotEmpty()
}

fun <T : Any> ChoosePropertiesState<T>.check(id: T): ChoosePropertiesState<T> =
    copy(
        checkableProperties = checkableProperties.map { option ->
            if (option.id == id) {
                option.copy(checked = !option.checked)
            } else {
                option
            }
        }
    )
