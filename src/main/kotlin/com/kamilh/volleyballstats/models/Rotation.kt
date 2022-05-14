package com.kamilh.volleyballstats.models

enum class Rotation(val value: Int) {
    P1(1), P2(2), P3(3), P4(4), P5(5), P6(6);
}

operator fun Rotation.inc(): Rotation =
    when (this) {
        Rotation.P1 -> Rotation.P2
        Rotation.P2 -> Rotation.P3
        Rotation.P3 -> Rotation.P4
        Rotation.P4 -> Rotation.P5
        Rotation.P5 -> Rotation.P6
        Rotation.P6 -> Rotation.P1
    }