package com.kamilh.volleyballstats.models

import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.models.PlayerId
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.Url

data class TeamPlayer(
    val id: PlayerId,
    val name: String,
    val imageUrl: Url?,
    val team: TeamId,
    val specialization: Specialization,
    val updatedAt: LocalDateTime,
) {

    override fun toString(): String {
        return "TeamPlayer(id=${id.value}, name='$name' team=${team.value})"
    }
}
