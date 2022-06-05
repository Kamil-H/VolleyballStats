package com.kamilh.volleyballstats.domain.models

import com.kamilh.volleyballstats.datetime.LocalDateTime

data class TeamPlayer(
    val id: PlayerId,
    val name: String,
    val imageUrl: Url?,
    val team: TeamId,
    val specialization: Specialization,
    val updatedAt: LocalDateTime,
) {
    enum class Specialization(val id: Int) {
        Setter(5), Libero(1), MiddleBlocker(4), OutsideHitter(2), OppositeHitter(3);

        companion object {
            fun create(id: Int): Specialization = values().first { it.id == id }
        }
    }

    override fun toString(): String {
        return "TeamPlayer(id=${id.value}, name='$name' team=${team.value})"
    }
}