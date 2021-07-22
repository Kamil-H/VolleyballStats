package com.kamilh.models

enum class Skill(val id: Char) {
    Attack('A'),
    Block('B'),
    Dig('D'),
    Set('E'),
    Freeball('F'),
    Receive('R'),
    Serve('S');

    companion object {

        fun create(id: String): Skill {
            val message = "Wrong Skill id=$id"
            return if (id.length != 1) {
                error(message)
            } else {
                val char = id.toCharArray().first()
                values().firstOrNull { it.id == char } ?: error(message)
            }
        }
    }
}