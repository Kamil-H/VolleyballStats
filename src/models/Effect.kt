package com.kamilh.models

// TODO: Need to find better names/description
enum class Effect(val id: Char) {
    Perfect('#'),
    Positive('+'),
    Negative('-'),
    Error('='),
    Half('/'),
    Invasion('!');

    companion object {

        fun create(id: String): Effect {
            val message = "Wrong Effect id=$id"
            return if (id.length != 1) {
                error(message)
            } else {
                val char = id.toCharArray().first()
                values().firstOrNull { it.id == char } ?: error(message)
            }
        }
    }
}