package com.kamilh.volleyballstats.presentation.extensions

import com.kamilh.volleyballstats.presentation.features.common.Property

inline fun <reified T> findProperty(property: Property<String>): T where T : Enum<T>, T : Property<String> =
    enumValues<T>().first { it.id == property.id }
