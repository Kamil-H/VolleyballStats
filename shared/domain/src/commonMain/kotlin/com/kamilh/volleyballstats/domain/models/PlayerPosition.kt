package com.kamilh.volleyballstats.domain.models

enum class PlayerPosition(val value: Int) {
    P1(1), P2(2), P3(3), P4(4), P5(5), P6(6);

    companion object {

        fun create(value: Int): PlayerPosition =
            values().firstOrNull { it.value == value } ?: error("Wrong PlayerPosition value=$value")
    }
}