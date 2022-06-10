package com.kamilh.volleyballstats.domain.models

enum class Specialization(val id: Int) {
    Setter(5), Libero(1), MiddleBlocker(4), OutsideHitter(2), OppositeHitter(3);

    companion object {
        fun create(id: Int): Specialization = values().first { it.id == id }
    }
}