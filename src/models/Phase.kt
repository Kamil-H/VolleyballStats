package com.kamilh.models

enum class Phase(val id: String) {
    PlayOff("Play Off"),
    RegularSeason("FZ");

    companion object {
        fun create(id: String): Phase = values().first { it.id == id }
    }
}