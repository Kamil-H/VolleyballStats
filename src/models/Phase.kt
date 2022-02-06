package com.kamilh.models

enum class Phase(val id: List<String>) {
    PlayOff(listOf("Play Off", "Finał")),
    RegularSeason(listOf("FZ", "ZAS", "Faza Zasadnicza"));

    companion object {
        fun create(id: String): Phase = values().first { it.id.contains(id) }
    }
}